package io.labs64.checkout.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.api.CustomerApi;
import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.mapper.CustomerMapper;
import io.labs64.checkout.model.Customer;
import io.labs64.checkout.model.CustomerCreateRequest;
import io.labs64.checkout.model.CustomerPage;
import io.labs64.checkout.model.CustomerUpdateRequest;
import io.labs64.checkout.service.CustomerService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RestController
public class CustomerController implements CustomerApi {
    private final RequestTenantProvider tenantProvider;
    private final CustomerService service;
    private final CustomerMapper mapper;

    @Override
    public ResponseEntity<Customer> getCustomer(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();

        final CustomerEntity entity = service.get(tenantId, id);
        final Customer response = mapper.toDto(entity);

        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<CustomerPage> listCustomers(final String query, final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();
        final Page<CustomerEntity> list = service.list(tenantId, query, pageable);
        final CustomerPage page = mapper.toPage(list);

        return ResponseEntity.ok().body(page);
    }

    @Override
    public ResponseEntity<Customer> createCustomer(final CustomerCreateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final CustomerEntity entity = service.create(tenantId, mapper.toEntity(request));
        final Customer response = mapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Customer> updateCustomer(final UUID id, final CustomerUpdateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final CustomerEntity entity = service.update(tenantId, id, (po) -> mapper.updateEntity(request, po));
        final Customer response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }
}
