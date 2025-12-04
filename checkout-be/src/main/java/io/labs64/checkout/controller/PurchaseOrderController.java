package io.labs64.checkout.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.api.PurchaseOrderApi;
import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.mapper.CheckoutTransactionMapper;
import io.labs64.checkout.mapper.PurchaseOrderMapper;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.CheckoutNextAction;
import io.labs64.checkout.model.CheckoutRequest;
import io.labs64.checkout.model.CheckoutResponse;
import io.labs64.checkout.model.PurchaseOrder;
import io.labs64.checkout.model.PurchaseOrderCreateRequest;
import io.labs64.checkout.model.PurchaseOrderPage;
import io.labs64.checkout.model.PurchaseOrderUpdateRequest;
import io.labs64.checkout.model.ShippingInfo;
import io.labs64.checkout.service.CustomerService;
import io.labs64.checkout.service.PurchaseOrderService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RestController
public class PurchaseOrderController implements PurchaseOrderApi {
    private final RequestTenantProvider tenantProvider;
    private final PurchaseOrderService service;
    private final CustomerService customerService;
    private final PurchaseOrderMapper mapper;
    private final CheckoutTransactionMapper transactionMapper;

    @Override
    public ResponseEntity<PurchaseOrder> getPurchaseOrder(final UUID id) {
        final PurchaseOrderEntity entity = service.get(id);
        final PurchaseOrder response = mapper.toDto(entity);

        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<PurchaseOrderPage> listPurchaseOrders(final String query, final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();
        final Page<PurchaseOrderEntity> list = service.list(tenantId, query, pageable);
        final PurchaseOrderPage page = mapper.toPage(list);

        return ResponseEntity.ok().body(page);
    }

    @Override
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(final PurchaseOrderCreateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final PurchaseOrderEntity newPurchaseOrder = mapper.toEntity(request);

        if (request.getCustomerId() != null) {
            final CustomerEntity customer = customerService.get(tenantId, request.getCustomerId());
            newPurchaseOrder.setCustomer(customer);
        }

        final PurchaseOrderEntity entity = service.create(tenantId, newPurchaseOrder);
        final PurchaseOrder response = mapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(final UUID id, final PurchaseOrderUpdateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final PurchaseOrderEntity entity = service.update(tenantId, id, (po) -> mapper.updateEntity(request, po));
        final PurchaseOrder response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CheckoutResponse> checkoutPurchaseOrder(final UUID id, final CheckoutRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final String paymentMethod = request.getPaymentMethod();
        final BillingInfo billingInfo = request.getBillingInfo();
        final ShippingInfo shippingInfo = request.getShippingInfo();

        final CheckoutTransactionEntity transaction = service.checkout(tenantId, id, paymentMethod, billingInfo,
                shippingInfo);

        // TODO(RVA): only for demonstration
        final CheckoutNextAction nextAction = new CheckoutNextAction(CheckoutNextAction.TypeEnum.NONE);

        final CheckoutResponse response = new CheckoutResponse();
        response.setTransaction(transactionMapper.toDto(transaction));
        response.setNextAction(nextAction);

        return ResponseEntity.ok().body(response);
    }
}
