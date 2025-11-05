package io.labs64.checkout.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import io.labs64.checkout.entity.ShippingInfoEntity;
import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.ConsentRequiredException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.ShippingInfoMessages;
import io.labs64.checkout.repository.ShippingInfoRepository;
import io.labs64.checkout.repository.ShippingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class ShippingInfoService {
    private final ShippingInfoRepository repository;
    private final ShippingRepository shippingRepository;
    private final ShippingInfoMessages msg;

    @Transactional(readOnly = true)
    public Optional<ShippingInfoEntity> find(final String tenantId, final UUID id) {
        // TODO(RVA): retrievable with publishable key?
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Transactional(readOnly = true)
    public ShippingInfoEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Transactional(readOnly = true)
    public Page<ShippingInfoEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.hasText(query)) {
            // TODO(RVA): implement query reader
            return Page.empty();
        }
        return repository.findByTenantId(tenantId, pageable);
    }

    @Transactional
    public ShippingInfoEntity create(final String tenantId, final ShippingInfoEntity entity) {
        entity.setTenantId(tenantId);

        if (entity.getConfirmedAt() == null) {
            throw new ConsentRequiredException(msg.consentRequired());
        }

        final ShippingInfoEntity saved = repository.save(entity);
        log.debug("Create shipping info: {}", saved);
        return saved;
    }

    @Transactional
    public ShippingInfoEntity create(final String tenantId, final ShippingInfoEntity entity, final Boolean confirmed) {
        if (confirmed && entity.getConfirmedAt() == null) {
            entity.setConfirmedAt(OffsetDateTime.now());
        }
        return create(tenantId, entity);
    }

    @Transactional
    public ShippingInfoEntity update(final String tenantId, final UUID id, final Consumer<ShippingInfoEntity> updater) {
        return repository.findByIdAndTenantId(id, tenantId).map((b) -> {
            updater.accept(b);

            log.debug("Update shipping info: {}", b);
            return b;
        }).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Transactional
    public boolean delete(final String tenantId, final UUID id) {
        if (shippingRepository.existsByTenantIdAndShippingInfoId(tenantId, id)) {
            throw new ConflictException(msg.deleteConflict(id));
        }

        final int affected = repository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Delete shipping info id={} tenant={} affected={}", id, tenantId, affected);
        return affected > 0;
    }
}
