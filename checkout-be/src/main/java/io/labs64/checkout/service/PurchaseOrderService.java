package io.labs64.checkout.service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.ShippingInfo;

public interface PurchaseOrderService {
    Optional<PurchaseOrderEntity> find(final UUID id);

    PurchaseOrderEntity get(final UUID id);

    Optional<PurchaseOrderEntity> find(final String tenantId, final UUID id);

    PurchaseOrderEntity get(final String tenantId, final UUID id);

    Page<PurchaseOrderEntity> list(final String tenantId, final String query, final Pageable pageable);

    PurchaseOrderEntity create(String tenantId, PurchaseOrderEntity entity);

    PurchaseOrderEntity update(final String tenantId, final UUID id, final Consumer<PurchaseOrderEntity> updater);

    boolean delete(final String tenantId, final UUID id);

    CheckoutTransactionEntity checkout(final String tenantId, final UUID id, final String paymentMethod,
            final BillingInfo billingInfo, final ShippingInfo shippingInfo);
}
