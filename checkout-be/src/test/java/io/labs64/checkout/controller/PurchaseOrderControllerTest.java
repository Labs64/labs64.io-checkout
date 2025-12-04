package io.labs64.checkout.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.mapper.CheckoutTransactionMapper;
import io.labs64.checkout.mapper.PurchaseOrderMapper;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.CheckoutNextAction;
import io.labs64.checkout.model.CheckoutRequest;
import io.labs64.checkout.model.CheckoutResponse;
import io.labs64.checkout.model.CheckoutTransaction;
import io.labs64.checkout.model.PurchaseOrder;
import io.labs64.checkout.model.PurchaseOrderCreateRequest;
import io.labs64.checkout.model.PurchaseOrderPage;
import io.labs64.checkout.model.PurchaseOrderUpdateRequest;
import io.labs64.checkout.model.ShippingInfo;
import io.labs64.checkout.service.CustomerService;
import io.labs64.checkout.service.PurchaseOrderService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderControllerTest {

    @Mock
    private RequestTenantProvider tenantProvider;

    @Mock
    private PurchaseOrderService service;

    @Mock
    private CustomerService customerService;

    @Mock
    private PurchaseOrderMapper mapper;

    @Mock
    private CheckoutTransactionMapper transactionMapper;

    @InjectMocks
    private PurchaseOrderController controller;

    @Test
    void getPurchaseOrderOk() {
        final UUID id = UUID.randomUUID();
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        final PurchaseOrder dto = new PurchaseOrder();

        when(service.get(id)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<PurchaseOrder> response = controller.getPurchaseOrder(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(service).get(id);
    }

    @Test
    void listPurchaseOrdersOk() {
        final String tenantId = "tenant-1";
        final String query = "status:DRAFT";
        final Pageable pageable = PageRequest.of(1, 20);

        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        final Page<PurchaseOrderEntity> entityPage =
                new PageImpl<>(List.of(entity), pageable, 1);

        final PurchaseOrderPage dtoPage = new PurchaseOrderPage();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.list(tenantId, query, pageable)).thenReturn(entityPage);
        when(mapper.toPage(entityPage)).thenReturn(dtoPage);

        final ResponseEntity<PurchaseOrderPage> response =
                controller.listPurchaseOrders(query, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dtoPage, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).list(tenantId, query, pageable);
    }

    @Test
    void createPurchaseOrderWithoutCustomer() {
        final String tenantId = "tenant-1";
        final PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(); // items/currency тут не важливі для контролера

        final PurchaseOrderEntity newPo = new PurchaseOrderEntity();
        final PurchaseOrderEntity created = new PurchaseOrderEntity();
        final PurchaseOrder dto = new PurchaseOrder();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(mapper.toEntity(request)).thenReturn(newPo);
        when(service.create(tenantId, newPo)).thenReturn(created);
        when(mapper.toDto(created)).thenReturn(dto);

        final ResponseEntity<PurchaseOrder> response =
                controller.createPurchaseOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).create(tenantId, newPo);
        verify(customerService, never()).get(anyString(), any(UUID.class));
    }

    @Test
    void createPurchaseOrderWithCustomer() {
        final String tenantId = "tenant-1";
        final UUID customerId = UUID.randomUUID();

        final PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest();
        request.setCustomerId(customerId);

        final PurchaseOrderEntity newPo = new PurchaseOrderEntity();
        final CustomerEntity customer = new CustomerEntity();
        final PurchaseOrderEntity created = new PurchaseOrderEntity();
        final PurchaseOrder dto = new PurchaseOrder();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(mapper.toEntity(request)).thenReturn(newPo);
        when(customerService.get(tenantId, customerId)).thenReturn(customer);
        when(service.create(tenantId, newPo)).thenReturn(created);
        when(mapper.toDto(created)).thenReturn(dto);

        final ResponseEntity<PurchaseOrder> response =
                controller.createPurchaseOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(dto, response.getBody());
        assertSame(customer, newPo.getCustomer());

        verify(tenantProvider).requireTenantId();
        verify(customerService).get(tenantId, customerId);
        verify(service).create(tenantId, newPo);
    }

    @Test
    void updatePurchaseOrderOk() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final PurchaseOrderUpdateRequest request = new PurchaseOrderUpdateRequest();

        final PurchaseOrderEntity updated = new PurchaseOrderEntity();
        final PurchaseOrder dto = new PurchaseOrder();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.update(eq(tenantId), eq(id), any())).thenReturn(updated);
        when(mapper.toDto(updated)).thenReturn(dto);

        final ResponseEntity<PurchaseOrder> response =
                controller.updatePurchaseOrder(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).update(eq(tenantId), eq(id), any());
    }

    @Test
    void checkoutPurchaseOrderOk() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();

        final CheckoutRequest request = new CheckoutRequest();
        final String paymentMethod = "card";
        final BillingInfo billingInfo = new BillingInfo();
        final ShippingInfo shippingInfo = new ShippingInfo();

        request.setPaymentMethod(paymentMethod);
        request.setBillingInfo(billingInfo);
        request.setShippingInfo(shippingInfo);

        final CheckoutTransactionEntity txEntity = new CheckoutTransactionEntity();
        final CheckoutTransaction txDto = new CheckoutTransaction();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.checkout(tenantId, id, paymentMethod, billingInfo, shippingInfo))
                .thenReturn(txEntity);
        when(transactionMapper.toDto(txEntity)).thenReturn(txDto);

        final ResponseEntity<CheckoutResponse> response =
                controller.checkoutPurchaseOrder(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        final CheckoutResponse body = response.getBody();
        assertNotNull(body);
        assertSame(txDto, body.getTransaction());

        assertNotNull(body.getNextAction());
        assertEquals(CheckoutNextAction.TypeEnum.NONE, body.getNextAction().getType());

        verify(tenantProvider).requireTenantId();
        verify(service).checkout(tenantId, id, paymentMethod, billingInfo, shippingInfo);
    }
}
