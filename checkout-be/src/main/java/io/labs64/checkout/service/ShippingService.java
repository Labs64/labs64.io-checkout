package io.labs64.checkout.service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import io.labs64.checkout.entity.CheckoutIntentEntity;
import io.labs64.checkout.entity.ShippingEntity;
import io.labs64.checkout.entity.ShippingInfoEntity;
import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.ShippingMessages;
import io.labs64.checkout.repository.CheckoutIntentRepository;
import io.labs64.checkout.repository.ShippingRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class ShippingService {
    private final ShippingRepository repository;
    private final ShippingInfoService shippingInfoService;
    private final CheckoutIntentService checkoutIntentService;
    private final CheckoutIntentRepository checkoutIntentRepository;
    private final ShippingMessages msg;

    @Transactional(readOnly = true)
    public Optional<ShippingEntity> find(final String tenantId, final UUID id) {
        // TODO(RVA): retrievable with publishable key?
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Transactional(readOnly = true)
    public ShippingEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Transactional(readOnly = true)
    public Page<ShippingEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.isNotBlank(query)) {
            // TODO(RVA): implement query reader
            return Page.empty();
        }
        return repository.findByTenantId(tenantId, pageable);
    }

    @Transactional
    public ShippingEntity create(@NotBlank final String tenantId, @NotBlank final UUID shippingInfoId,
            @NotBlank final UUID checkoutIntentId, final ShippingEntity entity) {
        entity.setTenantId(tenantId);

        final ShippingInfoEntity shippingInfo = shippingInfoService.get(tenantId, shippingInfoId);
        final CheckoutIntentEntity checkoutIntent = checkoutIntentService.get(tenantId, checkoutIntentId);

        entity.setShippingInfo(shippingInfo);
        entity.setCheckoutIntent(checkoutIntent);

        final ShippingEntity saved = repository.save(entity);

        log.debug("Create shipping: {}", saved);

        return saved;
    }

    @Transactional
    public ShippingEntity update(final String tenantId, final UUID id, final Consumer<ShippingEntity> updater) {
        return repository.findByIdAndTenantId(id, tenantId).map((b) -> {
            updater.accept(b);
            log.debug("Update shipping: {}", b);
            return b;
        }).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Transactional
    public boolean delete(final String tenantId, final UUID id) {
        if (checkoutIntentRepository.existsByTenantIdAndShippingId(tenantId, id)) {
            throw new ConflictException(msg.deleteConflict(id));
        }

        final int affected = repository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Delete shipping id={} tenant={} affected={}", id, tenantId, affected);
        return affected > 0;
    }
}
