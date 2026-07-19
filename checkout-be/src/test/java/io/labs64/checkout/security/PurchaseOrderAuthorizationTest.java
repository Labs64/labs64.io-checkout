package io.labs64.checkout.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import io.labs64.authcontext.authorization.AuthorizationDecision;
import io.labs64.authcontext.authorization.AuthorizationProperties;
import io.labs64.authcontext.authorization.AuthorizationService;
import io.labs64.authcontext.authorization.AuthorizeInterceptor;
import io.labs64.authcontext.authorization.ResourceEntity;
import io.labs64.authcontext.core.AuthContext;
import io.labs64.authcontext.core.AuthContextHolder;
import io.labs64.checkout.controller.PurchaseOrderController;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.model.CheckoutRequest;
import io.labs64.checkout.service.PurchaseOrderService;

/**
 * RFC-07 rename migration: exercises the checkout {@link PurchaseOrderResourceResolver}
 * + {@link AuthorizeInterceptor} against a stub {@link AuthorizationService} that
 * mirrors the Cerbos domain-policy semantics (scope-per-action + structural
 * tenant guard). The real Cerbos client is covered by the commons
 * {@code CerbosAuthorizationServiceTest}; decision equivalence for the whole
 * policy set is proven by the commons {@code auth-policy-cerbos} truth-table
 * gate. Here the tenant-guard cases matter most: getPurchaseOrder's lookup is
 * not tenant-scoped, so the resolver-supplied resource tenant is the layer that
 * blocks cross-tenant reads once enforcing.
 */
@ExtendWith(MockitoExtension.class)
class PurchaseOrderAuthorizationTest {

    private static final String TENANT = "t_100";
    private static final UUID ORDER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440077");

    @Mock
    private PurchaseOrderService purchaseOrderService;

    private final List<AuthorizationDecision> decisions = new ArrayList<>();
    private MockHttpServletResponse response;

    @BeforeEach
    void resetResponse() {
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void cleanup() {
        AuthContextHolder.clear();
    }

    /**
     * Stub PDP mirroring the generated checkout domain policy: each action needs
     * its scope, and the tenant guard denies whenever the resource carries a
     * tenant that differs from (or is absent on) the principal.
     */
    private static final class StubAuthorizationService implements AuthorizationService {

        private static final Map<String, String> REQUIRED_SCOPE = Map.of(
                "getPurchaseOrder", "purchase-order:read",
                "checkoutPurchaseOrder", "purchase-order:checkout",
                "updatePurchaseOrder", "purchase-order:write");

        private final AuthorizationProperties.Mode mode;

        StubAuthorizationService(final AuthorizationProperties.Mode mode) {
            this.mode = mode;
        }

        @Override
        public boolean isEnforcing() {
            return mode == AuthorizationProperties.Mode.ENFORCE;
        }

        @Override
        public AuthorizationDecision decide(final AuthContext ctx, final String action, final ResourceEntity resource) {
            Object tenant = resource.attributes().get("tenant");
            boolean tenantGuard = tenant == null || tenant.equals(ctx.tenantId());
            String required = REQUIRED_SCOPE.get(action);
            boolean scopeOk = required == null || ctx.hasScope(required);
            boolean allowed = tenantGuard && scopeOk;
            return new AuthorizationDecision(action, resource.type(), resource.id(),
                    allowed, isEnforcing(), allowed ? List.of("policy0") : List.of(), null,
                    ctx.userId(), ctx.tenantId(), ctx.requestId());
        }
    }

    private AuthorizeInterceptor interceptor(final AuthorizationProperties.Mode mode) {
        return new AuthorizeInterceptor(new StubAuthorizationService(mode),
                List.of(new PurchaseOrderResourceResolver(purchaseOrderService)),
                List.of(decisions::add));
    }

    private void stubOrder(final String tenantId) {
        PurchaseOrderEntity order = new PurchaseOrderEntity();
        order.setId(ORDER_ID);
        order.setTenantId(tenantId);
        when(purchaseOrderService.get(eq(ORDER_ID))).thenReturn(order);
    }

    private boolean invoke(final AuthorizeInterceptor interceptor, final String methodName) throws Exception {
        Method method = switch (methodName) {
            case "checkoutPurchaseOrder" ->
                PurchaseOrderController.class.getMethod(methodName, UUID.class, CheckoutRequest.class);
            case "updatePurchaseOrder" -> PurchaseOrderController.class.getMethod(methodName, UUID.class,
                    io.labs64.checkout.model.PurchaseOrderUpdateRequest.class);
            default -> PurchaseOrderController.class.getMethod(methodName, UUID.class);
        };
        HandlerMethod handler = new HandlerMethod(mock(PurchaseOrderController.class), method);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("id", ORDER_ID.toString()));
        return interceptor.preHandle(request, response, handler);
    }

    private void authenticate(final String... scopes) {
        AuthContextHolder.set(new AuthContext("alice", TENANT, Set.of(scopes), "r-1"));
    }

    @Test
    void enforceAllowsSameTenantReadWithScope() throws Exception {
        authenticate("purchase-order:read");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(AuthorizationProperties.Mode.ENFORCE), "getPurchaseOrder")).isTrue();
        assertThat(decisions.get(0).allowed()).isTrue();
    }

    @Test
    void enforceBlocksCrossTenantReadDespiteUnscopedLookup() throws Exception {
        // service.get(id) is NOT tenant-scoped — the tenant guard is the layer
        // that actually prevents the cross-tenant read.
        authenticate("purchase-order:read");
        stubOrder("t_999");
        assertThat(invoke(interceptor(AuthorizationProperties.Mode.ENFORCE), "getPurchaseOrder")).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void enforceRequiresCheckoutScopeForCheckout() throws Exception {
        authenticate("purchase-order:read");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(AuthorizationProperties.Mode.ENFORCE), "checkoutPurchaseOrder")).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void enforceAllowsCheckoutWithCheckoutScope() throws Exception {
        authenticate("purchase-order:checkout");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(AuthorizationProperties.Mode.ENFORCE), "checkoutPurchaseOrder")).isTrue();
    }

    @Test
    void enforceAllowsUpdateWithWriteScope() throws Exception {
        authenticate("purchase-order:write");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(AuthorizationProperties.Mode.ENFORCE), "updatePurchaseOrder")).isTrue();
    }

    @Test
    void shadowModeNeverBlocksCrossTenantButAuditsDeny() throws Exception {
        authenticate("purchase-order:read");
        stubOrder("t_999");
        assertThat(invoke(interceptor(AuthorizationProperties.Mode.SHADOW), "getPurchaseOrder")).isTrue();
        assertThat(decisions.get(0).allowed()).isFalse();
        assertThat(decisions.get(0).enforced()).isFalse();
    }
}
