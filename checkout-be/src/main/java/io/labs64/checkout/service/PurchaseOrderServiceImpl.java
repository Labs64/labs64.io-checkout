package io.labs64.checkout.service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.exception.ValidationException;
import io.labs64.checkout.messages.PurchaseOrderMessages;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.CheckoutTransactionStatus;
import io.labs64.checkout.model.ConsentDefinition;
import io.labs64.checkout.model.ShippingInfo;
import io.labs64.checkout.repository.PurchaseOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository repository;
    private final CheckoutTransactionService transactionService;
    private final PurchaseOrderMessages msg;

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderEntity> find(final UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderEntity get(final UUID id) {
        return find(id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderEntity> find(final String tenantId, final UUID id) {
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.isNotBlank(query)) {
            // TODO(RVA): implement query reader(NLQL)
            return Page.empty();
        }
        return repository.findByTenantId(tenantId, pageable);
    }

    @Override
    @Transactional
    public PurchaseOrderEntity create(final String tenantId, final PurchaseOrderEntity entity) {
        entity.setTenantId(tenantId);

        entity.getItems().forEach((i) -> i.setPurchaseOrder(entity));

        final PurchaseOrderEntity saved = repository.save(entity);

        log.debug("Create purchase order: {}", saved);
        return saved;
    }

    @Override
    @Transactional
    public PurchaseOrderEntity update(final String tenantId, final UUID id,
            final Consumer<PurchaseOrderEntity> updater) {
        return repository.findByIdAndTenantId(id, tenantId).map((entity) -> {
            updater.accept(entity);

            log.debug("Update purchase order: {}", entity);

            return entity;
        }).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional
    public boolean delete(final String tenantId, final UUID id) {
        final int affected = repository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Delete purchase order id={} tenant={} affected={}", id, tenantId, affected);
        return affected > 0;
    }

    @Override
    @Transactional
    public CheckoutTransactionEntity checkout(final String tenantId, final UUID id, final String paymentMethod,
            final BillingInfo billingInfo, final ShippingInfo shippingInfo, final Map<String, Boolean> consents,
            final Map<String, Object> extra) {
        final PurchaseOrderEntity purchaseOrder = get(tenantId, id);

        // validation
        validateCheckoutWindow(purchaseOrder);
        validateRequiredConsents(purchaseOrder, consents);

        return transactionService.create(tenantId,
                CheckoutTransactionEntity.builder().purchaseOrder(purchaseOrder).paymentMethod(paymentMethod)
                        .billingInfo(billingInfo).shippingInfo(shippingInfo).status(CheckoutTransactionStatus.PENDING)
                        .consents(consents).extra(extra).build());
    }

    private void validateCheckoutWindow(final PurchaseOrderEntity purchaseOrder) {
        final OffsetDateTime now = OffsetDateTime.now();
        final OffsetDateTime startsAt = purchaseOrder.getStartsAt();
        final OffsetDateTime endsAt = purchaseOrder.getEndsAt();

        if (startsAt != null && startsAt.isAfter(now)) {
            throw new ValidationException(msg.checkoutNotStarted(startsAt));
        }

        if (endsAt != null && endsAt.isBefore(now)) {
            throw new ValidationException(msg.checkoutExpired(endsAt));
        }
    }

    private void validateRequiredConsents(final PurchaseOrderEntity purchaseOrder,
            final Map<String, Boolean> consents) {
        if (purchaseOrder.getConsents() == null || purchaseOrder.getConsents().isEmpty()) {
            return;
        }

        final Map<String, Boolean> safeConsents = consents != null ? consents : Map.of();

        for (final ConsentDefinition consentDef : purchaseOrder.getConsents()) {
            if (!consentDef.getRequired()) {
                continue;
            }

            final String consentId = consentDef.getId();
            final Boolean accepted = safeConsents.get(consentId);

            if (!Boolean.TRUE.equals(accepted)) {
                throw new ValidationException(msg.requiredConsentNotAccepted(consentId));
            }
        }
    }
}
