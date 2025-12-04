package io.labs64.checkout.service;

import java.util.List;
import java.util.Optional;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.CheckoutTransactionMessages;
import io.labs64.checkout.repository.CheckoutTransactionRepository;

@ExtendWith(MockitoExtension.class)
class CheckoutTransactionServiceImplTest {

    @Mock
    private CheckoutTransactionRepository repository;

    @Mock
    private CheckoutTransactionMessages msg;

    @InjectMocks
    private CheckoutTransactionServiceImpl service;

    @Test
    void findExistingById() {
        final UUID id = UUID.randomUUID();
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        final Optional<CheckoutTransactionEntity> result = service.find(id);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
        verify(repository).findById(id);
    }

    @Test
    void getExistingById() {
        final UUID id = UUID.randomUUID();
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        final CheckoutTransactionEntity result = service.get(id);

        assertSame(entity, result);
        verify(repository).findById(id);
        verifyNoInteractions(msg);
    }

    @Test
    void getMissingById() {
        final UUID id = UUID.randomUUID();
        final String message = String.format("CTX %s not found", id);
        when(repository.findById(id)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn(message);

        final NotFoundException ex = assertThrows(NotFoundException.class, () -> service.get(id));

        assertEquals(message, ex.getMessage());
        verify(repository).findById(id);
        verify(msg).notFound(id);
    }

    @Test
    void findExistingByTenant() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final Optional<CheckoutTransactionEntity> result = service.find(tenantId, id);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
        verify(repository).findByIdAndTenantId(id, tenantId);
    }

    @Test
    void getExistingByTenant() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final CheckoutTransactionEntity result = service.get(tenantId, id);

        assertSame(entity, result);
        verify(repository).findByIdAndTenantId(id, tenantId);
        verifyNoInteractions(msg);
    }

    @Test
    void getMissingByTenant() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String message = String.format("CTX %s not found", id);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn(message);

        final NotFoundException ex = assertThrows(NotFoundException.class, () -> service.get(tenantId, id));

        assertEquals(message, ex.getMessage());
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(msg).notFound(id);
    }

    @Test
    void listWithoutQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        final Page<CheckoutTransactionEntity> page = new PageImpl<>(List.of(entity));

        when(repository.findByTenantId(tenantId, pageable)).thenReturn(page);

        final Page<CheckoutTransactionEntity> result = service.list(tenantId, null, pageable);

        assertSame(page, result);
        verify(repository).findByTenantId(tenantId, pageable);
    }

    @Test
    void listWithBlankQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<CheckoutTransactionEntity> page = Page.empty(pageable);

        when(repository.findByTenantId(tenantId, pageable)).thenReturn(page);

        final Page<CheckoutTransactionEntity> result = service.list(tenantId, "   ", pageable);

        assertSame(page, result);
        verify(repository).findByTenantId(tenantId, pageable);
    }

    @Test
    void listWithQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);

        final Page<CheckoutTransactionEntity> result = service.list(tenantId, "status:SUCCESS", pageable);

        assertTrue(result.isEmpty());
        verify(repository, never()).findByTenantId(anyString(), any());
    }

    @Test
    void createTransaction() {
        final String tenantId = "tenant-1";

        final CheckoutTransactionEntity entity = mock(CheckoutTransactionEntity.class);
        final CheckoutTransactionEntity saved = new CheckoutTransactionEntity();

        when(repository.save(entity)).thenReturn(saved);

        final CheckoutTransactionEntity result = service.create(tenantId, entity);

        assertSame(saved, result);
        verify(entity).setTenantId(tenantId);
        verify(repository).save(entity);
    }
}
