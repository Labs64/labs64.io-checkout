package io.labs64.checkout.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import io.labs64.authcontext.authorization.PlanExpr;
import io.labs64.authcontext.authorization.QueryPlan;
import io.labs64.authcontext.authorization.QueryPlanner;
import io.labs64.authcontext.core.AuthContext;
import io.labs64.authcontext.core.AuthContextHolder;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.messages.PurchaseOrderMessages;
import io.labs64.checkout.repository.PurchaseOrderRepository;

/**
 * RFC-07 Data-PEP pilot: {@code PurchaseOrderServiceImpl.list} turns the PDP
 * PlanResources answer into a JPA row filter. A {@code Conditional} plan flows
 * to {@code repository.findAll(Specification, Pageable)}; an {@code AlwaysDenied}
 * plan short-circuits to an empty page without touching the repository
 * (fail closed).
 */
class PurchaseOrderListPlanTest {

    private static final String TENANT = "t_100";

    private final PurchaseOrderRepository repository = mock(PurchaseOrderRepository.class);
    private final CheckoutTransactionService transactionService = mock(CheckoutTransactionService.class);
    private final PurchaseOrderMessages msg = mock(PurchaseOrderMessages.class);
    private final QueryPlanner queryPlanner = mock(QueryPlanner.class);
    private final PurchaseOrderServiceImpl service =
            new PurchaseOrderServiceImpl(repository, transactionService, msg, queryPlanner);

    private final Pageable pageable = PageRequest.of(0, 10);

    @AfterEach
    void cleanup() {
        AuthContextHolder.clear();
    }

    private void authenticate() {
        AuthContextHolder.set(new AuthContext("alice", TENANT, Set.of("purchase-order:read"), "r-1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void conditionalPlanBecomesRepositorySpecificationQuery() {
        authenticate();
        final QueryPlan plan = new QueryPlan.Conditional(new PlanExpr.Op("eq", List.of(
                new PlanExpr.Var("request.resource.attr.tenant"), new PlanExpr.Val(TENANT))));
        when(queryPlanner.plan(any(), eq("listPurchaseOrders"), eq("PurchaseOrder"))).thenReturn(plan);
        final Page<PurchaseOrderEntity> page = new PageImpl<>(List.of(new PurchaseOrderEntity()));
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        final Page<PurchaseOrderEntity> result = service.list(TENANT, null, pageable);

        assertThat(result).isSameAs(page);
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void alwaysDeniedPlanReturnsEmptyWithoutHittingRepository() {
        authenticate();
        when(queryPlanner.plan(any(), eq("listPurchaseOrders"), eq("PurchaseOrder")))
                .thenReturn(new QueryPlan.AlwaysDenied());

        final Page<PurchaseOrderEntity> result = service.list(TENANT, null, pageable);

        assertThat(result).isEmpty();
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}
