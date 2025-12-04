package io.labs64.checkout.controller;

import java.util.List;
import java.util.UUID;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.mapper.CheckoutTransactionMapper;
import io.labs64.checkout.model.CheckoutTransaction;
import io.labs64.checkout.model.CheckoutTransactionPage;
import io.labs64.checkout.service.CheckoutTransactionService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;

@ExtendWith(MockitoExtension.class)
class CheckoutTransactionControllerTest {

    @Mock
    private RequestTenantProvider tenantProvider;

    @Mock
    private CheckoutTransactionService service;

    @Mock
    private CheckoutTransactionMapper mapper;

    @InjectMocks
    private CheckoutTransactionController controller;

    @Test
    void getCheckoutTransactionOk() {
        final UUID id = UUID.randomUUID();
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        final CheckoutTransaction dto = new CheckoutTransaction();

        when(service.get(id)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<CheckoutTransaction> response = controller.getCheckoutTransaction(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(service).get(id);
    }

    @Test
    void listCheckoutTransactionsOk() {
        final String tenantId = "tenant-1";
        final String query = "status:SUCCESS";
        final Pageable pageable = PageRequest.of(0, 10);

        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        final Page<CheckoutTransactionEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);

        final CheckoutTransactionPage dtoPage = new CheckoutTransactionPage();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.list(tenantId, query, pageable)).thenReturn(entityPage);
        when(mapper.toPage(entityPage)).thenReturn(dtoPage);

        final ResponseEntity<CheckoutTransactionPage> response = controller.listCheckoutTransactions(query, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dtoPage, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).list(eq(tenantId), eq(query), eq(pageable));
    }
}
