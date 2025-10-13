package io.labs64.checkout.v1.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.ConsentRequiredException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.BillingInfoMessages;
import io.labs64.checkout.rules.BillingInfoLockedSnapshot;
import io.labs64.checkout.rules.CheckoutIntentStatusRules;
import io.labs64.checkout.v1.entity.BillingInfoEntity;
import io.labs64.checkout.v1.repository.BillingInfoRepository;
import io.labs64.checkout.v1.repository.CheckoutIntentRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class BillingInfoService {
    private final BillingInfoRepository repository;
    private final CheckoutIntentRepository checkoutIntentRepository;
    private final BillingInfoMessages msg;

    @Transactional(readOnly = true)
    public Optional<BillingInfoEntity> find(final String tenantId, final UUID id) {
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Transactional(readOnly = true)
    public BillingInfoEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFoundById(id)));
    }

    @Transactional(readOnly = true)
    public Page<BillingInfoEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.isNotBlank(query)) {
            // TODO(RVA): implement query reader(NLQL)
            return Page.empty();
        }
        return repository.findByTenantId(tenantId, pageable);
    }

    @Transactional
    public BillingInfoEntity create(final String tenantId, final BillingInfoEntity entity) {
        entity.setTenantId(tenantId);

        if (entity.getConfirmedAt() == null) {
            throw new ConsentRequiredException(msg.consentRequired());
        }

        final BillingInfoEntity saved = repository.save(entity);
        log.debug("Create billing info: {}", saved);
        return saved;
    }

    @Transactional
    public BillingInfoEntity create(final String tenantId, final BillingInfoEntity entity, final boolean confirmed) {
        if (confirmed && entity.getConfirmedAt() == null) {
            entity.setConfirmedAt(OffsetDateTime.now());
        }
        return create(tenantId, entity);
    }

    @Transactional
    public BillingInfoEntity update(final String tenantId, final UUID id, final Consumer<BillingInfoEntity> updater) {
        return repository.findByIdAndTenantId(id, tenantId).map((b) -> {
            final boolean locked = checkoutIntentRepository.existsByTenantIdAndBillingInfoIdAndStatusIn(tenantId, id,
                    CheckoutIntentStatusRules.finishedStatuses());

            if (locked) {
                final BillingInfoLockedSnapshot before = BillingInfoLockedSnapshot.from(b);

                updater.accept(b);

                final BillingInfoLockedSnapshot after = BillingInfoLockedSnapshot.from(b);

                if (!before.equals(after)) {
                    throw new ConflictException(msg.lockedByTerminalCheckoutIntent(id));
                }

            } else {
                updater.accept(b);
            }

            log.debug("Update billing info: {}", b);
            return b;
        }).orElseThrow(() -> new NotFoundException(msg.notFoundById(id)));
    }

    @Transactional
    public boolean delete(final String tenantId, final UUID id) {
        final boolean linked = checkoutIntentRepository.existsByTenantIdAndBillingInfoId(tenantId, id);

        if (linked) {
            throw new ConflictException(msg.deleteConflictLinkedToCheckoutIntents(id));
        }

        final int affected = repository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Delete billing info id={} tenant={} affected={}", id, tenantId, affected);
        return affected > 0;
    }
}
