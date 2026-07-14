package io.labs64.checkout.security;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import io.labs64.authcontext.cedar.CedarEntity;
import io.labs64.authcontext.cedar.CedarEntityResolver;
import io.labs64.authcontext.core.AuthContext;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.service.PurchaseOrderService;

/**
 * Supplies the Cedar {@code PurchaseOrder} resource for {@code @Authorize}
 * checks. Deliberately uses the tenant-agnostic lookup: the
 * entity's REAL tenant feeds the Cedar tenant guard, which is the structural
 * backstop for handlers whose own lookup is not tenant-scoped
 * (getPurchaseOrder fetches by id only).
 */
@Component
public class PurchaseOrderCedarEntityResolver implements CedarEntityResolver {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderCedarEntityResolver(final PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @Override
    public boolean supports(final String resourceType) {
        return "PurchaseOrder".equals(resourceType);
    }

    @Override
    public CedarEntity resolve(final String resourceType, @Nullable final Object resourceRef,
            final AuthContext context) {
        final UUID id = UUID.fromString(String.valueOf(resourceRef));
        final PurchaseOrderEntity purchaseOrder = purchaseOrderService.get(id); // module 404 on miss
        final CedarEntity tenant = CedarEntity.ref("Tenant", purchaseOrder.getTenantId());
        return CedarEntity.builder("PurchaseOrder", purchaseOrder.getId().toString())
                .attribute("tenant", tenant)
                .parent(tenant)
                .build();
    }
}
