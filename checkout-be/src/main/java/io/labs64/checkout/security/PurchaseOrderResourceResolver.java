package io.labs64.checkout.security;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import io.labs64.authcontext.authorization.ResourceEntity;
import io.labs64.authcontext.authorization.ResourceResolver;
import io.labs64.authcontext.core.AuthContext;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.service.PurchaseOrderService;

/**
 * Supplies the {@code PurchaseOrder} resource for {@code @Authorize} checks.
 * Deliberately uses the tenant-agnostic lookup: the entity's REAL tenant feeds
 * the PDP tenant guard, which is the structural backstop for handlers whose own
 * lookup is not tenant-scoped (getPurchaseOrder fetches by id only).
 */
@Component
public class PurchaseOrderResourceResolver implements ResourceResolver {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderResourceResolver(final PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @Override
    public boolean supports(final String resourceType) {
        return "PurchaseOrder".equals(resourceType);
    }

    @Override
    public ResourceEntity resolve(final String resourceType, @Nullable final Object resourceRef,
            final AuthContext context) {
        if (resourceRef == null) {
            return ResourceEntity.builder(resourceType, "collection")
                    .attribute("tenant", context.tenantId())
                    .build();
        }

        final UUID id = UUID.fromString(String.valueOf(resourceRef));
        final PurchaseOrderEntity purchaseOrder = purchaseOrderService.get(id); // module 404 on miss
        return ResourceEntity.builder(resourceType, purchaseOrder.getId().toString())
                .attribute("tenant", purchaseOrder.getTenantId())
                .build();
    }
}
