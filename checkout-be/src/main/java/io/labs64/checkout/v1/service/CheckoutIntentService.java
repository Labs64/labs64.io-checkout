package io.labs64.checkout.v1.service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.CheckoutIntentMessages;
import io.labs64.checkout.rules.CheckoutIntentStatusRules;
import io.labs64.checkout.v1.entity.CheckoutIntentEntity;
import io.labs64.checkout.v1.repository.CheckoutIntentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class CheckoutIntentService {
    private final CheckoutIntentRepository repository;
    private final CheckoutIntentMessages msg;

    @Transactional(readOnly = true)
    public Optional<CheckoutIntentEntity> find(final String tenantId, final UUID id) {
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Transactional(readOnly = true)
    public CheckoutIntentEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Transactional(readOnly = true)
    public Page<CheckoutIntentEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.hasText(query)) {
            // TODO(RVA): implement query reader
            return Page.empty();
        }
        return repository.findByTenantId(tenantId, pageable);
    }

    @Transactional
    public CheckoutIntentEntity create(final String tenantId, final CheckoutIntentEntity entity) {
        entity.setTenantId(tenantId);

        final CheckoutIntentEntity saved = repository.save(entity);
        log.debug("Create checkout intent: {}", saved);

        return saved;
    }

    @Transactional
    public CheckoutIntentEntity update(final String tenantId, final UUID id,
            final Consumer<CheckoutIntentEntity> updater) {
        return repository.findByIdAndTenantId(id, tenantId).map((ci) -> {
            if (CheckoutIntentStatusRules.finishedStatuses().contains(ci.getStatus())) {
                throw new ConflictException(msg.cannotUpdateFinished(id, ci.getStatus()));
            }

            updater.accept(ci);
            log.debug("Checkout intent info: {}", ci);
            return ci;
        }).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }
}
