package io.labs64.checkout.service;

import java.time.OffsetDateTime;
import java.util.Collections;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.exception.ValidationException;
import io.labs64.checkout.messages.PurchaseOrderMessages;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.ShippingInfo;
import io.labs64.checkout.repository.PurchaseOrderRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceImplTest {

    @Mock
    private PurchaseOrderRepository repository;

    @Mock
    private CheckoutTransactionService transactionService;

    @Mock
    private PurchaseOrderMessages msg;

    @InjectMocks
    private PurchaseOrderServiceImpl service;

    @Test
    void findExistingById() {
        final UUID id = UUID.randomUUID();
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        final Optional<PurchaseOrderEntity> result = service.find(id);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
        verify(repository).findById(id);
    }

    @Test
    void getExistingById() {
        final UUID id = UUID.randomUUID();
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        final PurchaseOrderEntity result = service.get(id);

        assertSame(entity, result);
        verify(repository).findById(id);
        verifyNoInteractions(msg);
    }

    @Test
    void getMissingById() {
        final UUID id = UUID.randomUUID();
        final String message = String.format("PO %s not found", id);
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
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final Optional<PurchaseOrderEntity> result = service.find(tenantId, id);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
        verify(repository).findByIdAndTenantId(id, tenantId);
    }

    @Test
    void getExistingByTenant() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final PurchaseOrderEntity result = service.get(tenantId, id);

        assertSame(entity, result);
        verify(repository).findByIdAndTenantId(id, tenantId);
        verifyNoInteractions(msg);
    }

    @Test
    void getMissingByTenant() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String message = String.format("PO %s not found", id);
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
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        final Page<PurchaseOrderEntity> page = new PageImpl<>(List.of(entity));

        when(repository.findByTenantId(tenantId, pageable)).thenReturn(page);

        final Page<PurchaseOrderEntity> result = service.list(tenantId, null, pageable);

        assertSame(page, result);
        verify(repository).findByTenantId(tenantId, pageable);
    }

    @Test
    void listWithBlankQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<PurchaseOrderEntity> page = Page.empty(pageable);

        when(repository.findByTenantId(tenantId, pageable)).thenReturn(page);

        final Page<PurchaseOrderEntity> result = service.list(tenantId, "   ", pageable);

        assertSame(page, result);
        verify(repository).findByTenantId(tenantId, pageable);
    }

    @Test
    void listWithQuery() {
        final String tenantId = "tenant-1";
        final Pageable pageable = PageRequest.of(0, 10);

        final Page<PurchaseOrderEntity> result = service.list(tenantId, "status:DRAFT", pageable);

        assertTrue(result.isEmpty());
        verify(repository, never()).findByTenantId(anyString(), any());
    }

    @Test
    void createOrder() {
        final String tenantId = "tenant-1";

        final PurchaseOrderEntity entity = mock(PurchaseOrderEntity.class);
        final PurchaseOrderEntity saved = new PurchaseOrderEntity();

        when(entity.getItems()).thenReturn(Collections.emptyList());
        when(repository.save(entity)).thenReturn(saved);

        final PurchaseOrderEntity result = service.create(tenantId, entity);

        assertSame(saved, result);
        verify(entity).setTenantId(tenantId);
        verify(entity).getItems();
        verify(repository).save(entity);
    }

    @Test
    void updateExistingOrder() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final PurchaseOrderEntity entity = new PurchaseOrderEntity();
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(entity));

        final AtomicBoolean updaterCalled = new AtomicBoolean(false);

        final PurchaseOrderEntity result = service.update(tenantId, id, po -> updaterCalled.set(true));

        assertSame(entity, result);
        assertTrue(updaterCalled.get());
        verify(repository).findByIdAndTenantId(id, tenantId);
    }

    @Test
    void updateMissingOrder() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String message = String.format("PO %s not found", id);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn(message);

        final NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(tenantId, id, po -> {
        }));

        assertEquals(message, ex.getMessage());
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(msg).notFound(id);
    }

    @Test
    void deleteExistingOrder() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        when(repository.deleteByIdAndTenantId(id, tenantId)).thenReturn(1);

        final boolean result = service.delete(tenantId, id);

        assertTrue(result);
        verify(repository).deleteByIdAndTenantId(id, tenantId);
    }

    @Test
    void deleteMissingOrder() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        when(repository.deleteByIdAndTenantId(id, tenantId)).thenReturn(0);

        final boolean result = service.delete(tenantId, id);

        assertFalse(result);
        verify(repository).deleteByIdAndTenantId(id, tenantId);
    }

    @Test
    void checkoutValid() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String paymentMethod = "card";
        final BillingInfo billingInfo = new BillingInfo();
        final ShippingInfo shippingInfo = new ShippingInfo();

        final PurchaseOrderEntity po = mock(PurchaseOrderEntity.class);
        // no time limits
        when(po.getStartsAt()).thenReturn(null);
        when(po.getEndsAt()).thenReturn(null);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(po));

        final CheckoutTransactionEntity expected = new CheckoutTransactionEntity();
        when(transactionService.create(eq(tenantId), any(CheckoutTransactionEntity.class))).thenReturn(expected);

        final CheckoutTransactionEntity result = service.checkout(tenantId, id, paymentMethod, billingInfo,
                shippingInfo);

        assertSame(expected, result);
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(transactionService).create(eq(tenantId), any(CheckoutTransactionEntity.class));
    }

    @Test
    void checkoutNotStarted() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String paymentMethod = "card";
        final BillingInfo billingInfo = new BillingInfo();
        final ShippingInfo shippingInfo = new ShippingInfo();

        final PurchaseOrderEntity po = mock(PurchaseOrderEntity.class);
        final OffsetDateTime startsAt = OffsetDateTime.now().plusHours(1);
        final String message = String.format("checkout not started at %s", startsAt);
        when(po.getStartsAt()).thenReturn(startsAt);
        when(po.getEndsAt()).thenReturn(null);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(po));

        when(msg.checkoutNotStarted(startsAt)).thenReturn(message);

        final ValidationException ex = assertThrows(ValidationException.class,
                () -> service.checkout(tenantId, id, paymentMethod, billingInfo, shippingInfo));

        assertEquals(message, ex.getMessage());
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(msg).checkoutNotStarted(startsAt);
        verifyNoInteractions(transactionService);
    }

    @Test
    void checkoutExpired() {
        final String tenantId = "tenant-1";
        final UUID id = UUID.randomUUID();
        final String paymentMethod = "card";
        final BillingInfo billingInfo = new BillingInfo();
        final ShippingInfo shippingInfo = new ShippingInfo();

        final PurchaseOrderEntity po = mock(PurchaseOrderEntity.class);
        final OffsetDateTime endsAt = OffsetDateTime.now().minusHours(1);
        final String message = String.format("checkout expired at %s", endsAt);
        when(po.getStartsAt()).thenReturn(null);
        when(po.getEndsAt()).thenReturn(endsAt);
        when(repository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.of(po));

        when(msg.checkoutExpired(endsAt)).thenReturn(message);

        final ValidationException ex = assertThrows(ValidationException.class,
                () -> service.checkout(tenantId, id, paymentMethod, billingInfo, shippingInfo));

        assertEquals(message, ex.getMessage());
        verify(repository).findByIdAndTenantId(id, tenantId);
        verify(msg).checkoutExpired(endsAt);
        verifyNoInteractions(transactionService);
    }
}
