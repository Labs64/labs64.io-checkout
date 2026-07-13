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
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import io.labs64.authcontext.cedar.AuthorizationDecision;
import io.labs64.authcontext.cedar.AuthorizeInterceptor;
import io.labs64.authcontext.cedar.CedarAuthorizationService;
import io.labs64.authcontext.cedar.CedarProperties;
import io.labs64.authcontext.core.AuthContext;
import io.labs64.authcontext.core.AuthContextHolder;
import io.labs64.checkout.controller.PurchaseOrderController;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.model.CheckoutRequest;
import io.labs64.checkout.service.PurchaseOrderService;

/**
 * RFC-05 P4 fan-out: the REAL checkout domain policy set (generated from
 * OpenAPI x-labs64-auth, {@code classpath:auth-policy-domain.cedar}) + resolver + PEP.
 * The tenant-guard cases matter most here: getPurchaseOrder's lookup is not
 * tenant-scoped, so Cedar is the layer that actually blocks cross-tenant
 * reads once enforcing.
 */
@ExtendWith(MockitoExtension.class)
class PurchaseOrderCedarAuthorizationTest {

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

    private AuthorizeInterceptor interceptor(final CedarProperties.Mode mode) {
        CedarProperties properties = new CedarProperties();
        properties.setEnabled(true);
        properties.setMode(mode);
        CedarAuthorizationService service = new CedarAuthorizationService(properties,
                new ClassPathResource("auth-policy-domain.cedar"));
        return new AuthorizeInterceptor(service,
                List.of(new PurchaseOrderCedarEntityResolver(purchaseOrderService)),
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
        assertThat(invoke(interceptor(CedarProperties.Mode.ENFORCE), "getPurchaseOrder")).isTrue();
        assertThat(decisions.get(0).allowed()).isTrue();
    }

    @Test
    void enforceBlocksCrossTenantReadDespiteUnscopedLookup() throws Exception {
        // service.get(id) is NOT tenant-scoped — the Cedar guard is the layer
        // that actually prevents the cross-tenant read.
        authenticate("purchase-order:read");
        stubOrder("t_999");
        assertThat(invoke(interceptor(CedarProperties.Mode.ENFORCE), "getPurchaseOrder")).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void enforceRequiresCheckoutScopeForCheckout() throws Exception {
        authenticate("purchase-order:read");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(CedarProperties.Mode.ENFORCE), "checkoutPurchaseOrder")).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void enforceAllowsCheckoutWithCheckoutScope() throws Exception {
        authenticate("purchase-order:checkout");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(CedarProperties.Mode.ENFORCE), "checkoutPurchaseOrder")).isTrue();
    }

    @Test
    void enforceAllowsUpdateWithWriteScope() throws Exception {
        authenticate("purchase-order:write");
        stubOrder(TENANT);
        assertThat(invoke(interceptor(CedarProperties.Mode.ENFORCE), "updatePurchaseOrder")).isTrue();
    }

    @Test
    void shadowModeNeverBlocksCrossTenantButAuditsDeny() throws Exception {
        authenticate("purchase-order:read");
        stubOrder("t_999");
        assertThat(invoke(interceptor(CedarProperties.Mode.SHADOW), "getPurchaseOrder")).isTrue();
        assertThat(decisions.get(0).allowed()).isFalse();
        assertThat(decisions.get(0).enforced()).isFalse();
    }
}
