// checkout domain Cedar policy set (RFC-05 P4 fan-out, Tier 2).
//
// Validated in CI against the ONE shared schema
// (labs64.io-commons/auth-policy-cedar/schema.cedarschema); evaluated
// in-process by the @Authorize PEP (auth-context-spring-boot-starter).

// ── Tenant isolation guard (F4): no principal may act on another tenant's
// purchase order; forbid overrides any permit below. This is the structural
// backstop for lookups that are not tenant-scoped (e.g. getPurchaseOrder
// fetches by id only).
forbid(principal, action, resource is Labs64IO::PurchaseOrder)
unless { principal in resource.tenant };

// ── Read requires the read scope ──
permit(
  principal,
  action == Labs64IO::Action::"readPurchaseOrder",
  resource
) when { context.scopes.contains("purchase-order:read") };

// ── Update requires the write scope ──
permit(
  principal,
  action == Labs64IO::Action::"updatePurchaseOrder",
  resource
) when { context.scopes.contains("purchase-order:write") };

// ── Checkout (the money operation) requires the dedicated checkout scope ──
// (No workflow-state condition yet: PurchaseOrderEntity carries no status.)
permit(
  principal,
  action == Labs64IO::Action::"checkoutPurchaseOrder",
  resource
) when { context.scopes.contains("purchase-order:checkout") };
