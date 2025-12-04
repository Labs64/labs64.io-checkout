package io.labs64.checkout.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.CustomerMessages;
import io.labs64.checkout.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMessages msg;

    @InjectMocks
    private CustomerServiceImpl service;

    @Test
    void findExistingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final CustomerEntity entity = new CustomerEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final Optional<CustomerEntity> result = service.find(tenantId, id);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
        verify(repository).findByIdAndTenantId(id, tenantId);
    }

    @Test
    void getExistingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final CustomerEntity entity = new CustomerEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final CustomerEntity result = service.get(tenantId, id);

        assertSame(entity, result);
        verify(repository).findByIdAndTenantId(id, tenantId);
        verifyNoInteractions(msg);
    }

    @Test
    void getMissingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String message = String.format("Customer %s not found", id);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn(message);

        final NotFoundException ex = assertThrows(NotFoundException.class, () -> service.get(tenantId, id));

        assertEquals(message, ex.getMessage());
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(msg).notFound(id);
    }

    @Test
    void listNoQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);
        final CustomerEntity entity = new CustomerEntity();
        final Page<CustomerEntity> page = new PageImpl<>(List.of(entity));

        when(repository.findByTenantId(tenantId, pageable)).thenReturn(page);

        final Page<CustomerEntity> result = service.list(tenantId, null, pageable);

        assertSame(page, result);
        verify(repository).findByTenantId(tenantId, pageable);
    }

    @Test
    void listBlankQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<CustomerEntity> page = Page.empty(pageable);

        when(repository.findByTenantId(tenantId, pageable)).thenReturn(page);

        final Page<CustomerEntity> result = service.list(tenantId, "   ", pageable);

        assertSame(page, result);
        verify(repository).findByTenantId(tenantId, pageable);
    }

    @Test
    void listWithQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);

        final Page<CustomerEntity> result = service.list(tenantId, "email:john", pageable);

        assertTrue(result.isEmpty());
        verify(repository, never()).findByTenantId(anyString(), any());
    }

    @Test
    void createCustomer() {
        final String tenantId = "tenant-1";
        final CustomerEntity toSave = spy(new CustomerEntity());
        final CustomerEntity saved = new CustomerEntity();

        when(repository.save(toSave)).thenReturn(saved);

        final CustomerEntity result = service.create(tenantId, toSave);

        assertSame(saved, result);
        verify(toSave).setTenantId(tenantId);
        verify(repository).save(toSave);
    }

    @Test
    void updateExistingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final CustomerEntity entity = new CustomerEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final AtomicBoolean updaterCalled = new AtomicBoolean(false);

        final CustomerEntity result = service.update(tenantId, id, e -> updaterCalled.set(true));

        assertSame(entity, result);
        assertTrue(updaterCalled.get());
        verify(repository).findByIdAndTenantId(id, tenantId);
    }

    @Test
    void updateMissingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String message = String.format("Customer %s not found", id);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn(message);

        final NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(tenantId, id, e -> {
        }));

        assertEquals(message, ex.getMessage());
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(msg).notFound(id);
    }

    @Test
    void deleteExistingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        when(repository.deleteByIdAndTenantId(id, tenantId)).thenReturn(1);

        final boolean result = service.delete(tenantId, id);

        assertTrue(result);
        verify(repository).deleteByIdAndTenantId(id, tenantId);
    }

    @Test
    void deleteMissingCustomer() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        when(repository.deleteByIdAndTenantId(id, tenantId)).thenReturn(0);

        final boolean result = service.delete(tenantId, id);

        assertFalse(result);
        verify(repository).deleteByIdAndTenantId(id, tenantId);
    }
}
