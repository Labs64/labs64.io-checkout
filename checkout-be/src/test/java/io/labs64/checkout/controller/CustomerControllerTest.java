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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.mapper.CustomerMapper;
import io.labs64.checkout.model.Customer;
import io.labs64.checkout.model.CustomerCreateRequest;
import io.labs64.checkout.model.CustomerPage;
import io.labs64.checkout.model.CustomerUpdateRequest;
import io.labs64.checkout.service.CustomerService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private RequestTenantProvider tenantProvider;

    @Mock
    private CustomerService service;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerController controller;

    @Test
    void getCustomerOk() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();

        final CustomerEntity entity = new CustomerEntity();
        final Customer dto = new Customer();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.get(tenantId, id)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<Customer> response = controller.getCustomer(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).get(tenantId, id);
    }

    @Test
    void listCustomersOk() {
        final String tenantId = "tenant-1";
        final String query = "email:john";
        final Pageable pageable = PageRequest.of(0, 10);

        final CustomerEntity entity = new CustomerEntity();
        final Page<CustomerEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);

        final CustomerPage dtoPage = new CustomerPage();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.list(tenantId, query, pageable)).thenReturn(entityPage);
        when(mapper.toPage(entityPage)).thenReturn(dtoPage);

        final ResponseEntity<CustomerPage> response = controller.listCustomers(query, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dtoPage, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).list(tenantId, query, pageable);
    }

    @Test
    void createCustomerCreated() {
        final String tenantId = "tenant-1";
        final CustomerCreateRequest request = new CustomerCreateRequest("Doe");

        final CustomerEntity entityToCreate = new CustomerEntity();
        final CustomerEntity createdEntity = new CustomerEntity();
        final Customer dto = new Customer();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(mapper.toEntity(request)).thenReturn(entityToCreate);
        when(service.create(tenantId, entityToCreate)).thenReturn(createdEntity);
        when(mapper.toDto(createdEntity)).thenReturn(dto);

        final ResponseEntity<Customer> response = controller.createCustomer(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(mapper).toEntity(request);
        verify(service).create(tenantId, entityToCreate);
    }

    @Test
    void updateCustomerOk() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final CustomerUpdateRequest request = new CustomerUpdateRequest();

        final CustomerEntity existing = new CustomerEntity();
        final Customer dto = new Customer();

        when(tenantProvider.requireTenantId()).thenReturn(tenantId);
        when(service.update(any(), any(), any())).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(dto);

        final ResponseEntity<Customer> response = controller.updateCustomer(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        verify(tenantProvider).requireTenantId();
        verify(service).update(eq(tenantId), eq(id), any());
    }
}
