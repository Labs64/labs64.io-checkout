package io.labs64.checkout.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.api.CheckoutTransactionApi;
import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.mapper.CheckoutTransactionMapper;
import io.labs64.checkout.model.CheckoutTransaction;
import io.labs64.checkout.model.CheckoutTransactionPage;
import io.labs64.checkout.service.CheckoutTransactionService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RestController
public class CheckoutTransactionController implements CheckoutTransactionApi {
    private final RequestTenantProvider tenantProvider;
    private final CheckoutTransactionService service;
    private final CheckoutTransactionMapper mapper;

    @Override
    public ResponseEntity<CheckoutTransaction> getCheckoutTransaction(final UUID id) {
        final CheckoutTransactionEntity entity = service.get(id);
        final CheckoutTransaction response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CheckoutTransactionPage> listCheckoutTransactions(final String query,
            final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();
        final Page<CheckoutTransactionEntity> list = service.list(tenantId, query, pageable);
        final CheckoutTransactionPage page = mapper.toPage(list);

        return ResponseEntity.ok().body(page);
    }
}
