package io.labs64.checkout.mapper;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.entity.PurchaseOrderItemEntity;
import io.labs64.checkout.mapper.PurchaseOrderMapperImpl;
import io.labs64.checkout.model.PurchaseOrder;
import io.labs64.checkout.model.PurchaseOrderCreateRequest;
import io.labs64.checkout.model.PurchaseOrderItem;
import io.labs64.checkout.model.PurchaseOrderPage;
import io.labs64.checkout.model.PurchaseOrderUpdateRequest;
import io.labs64.checkout.util.AmountCalculator;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderMapperTest {

    @Mock
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Mock
    private CurrencyMapper currencyMapper;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private PurchaseOrderMapperImpl mapper;

    @Test
    void toEntityAllFieldsMapped() {
        final PurchaseOrderItem item = new PurchaseOrderItem("Pro plan", 1999L, 3);
        final List<PurchaseOrderItem> items = List.of(item);

        final Map<String, Object> extra = new HashMap<>();
        extra.put("source", "web_portal");
        extra.put("promoCode", "SUMMER2025");

        final OffsetDateTime startsAt = OffsetDateTime.now().plusDays(1);
        final OffsetDateTime endsAt = OffsetDateTime.now().plusDays(10);

        final String currency = "USD";

        final PurchaseOrderCreateRequest source = new PurchaseOrderCreateRequest(items);
        source.setStartsAt(startsAt);
        source.setEndsAt(endsAt);
        source.setExtra(extra);

        // stub item mapper to avoid NPE in @AfterMapping
        final PurchaseOrderItemEntity itemEntity = new PurchaseOrderItemEntity();
        when(purchaseOrderItemMapper.toEntity(anyList())).thenReturn(List.of(itemEntity));

        when(currencyMapper.upperCurrency(currency)).thenReturn(currency);

        final PurchaseOrderEntity result = mapper.toEntity(source);

        assertNotNull(result);

        assertNull(result.getId());
        assertNull(result.getTenantId());
        assertNull(result.getCustomer());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());

        assertEquals(startsAt, result.getStartsAt());
        assertEquals(endsAt, result.getEndsAt());
        assertEquals(extra, result.getExtra());
        assertEquals(currency, result.getCurrency());

        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertSame(itemEntity, result.getItems().getFirst());

        assertSame(result, itemEntity.getPurchaseOrder());
    }

    @Test
    void updateEntityExtraOnly() {
        final UUID id = UUID.randomUUID();
        final String tenantId = "tenant-1";

        final Map<String, Object> originalExtra = new HashMap<>();
        originalExtra.put("source", "old");

        final OffsetDateTime createdAt = OffsetDateTime.now().minusDays(3);
        final OffsetDateTime updatedAt = OffsetDateTime.now().minusDays(1);
        final OffsetDateTime startsAt = OffsetDateTime.now().plusDays(1);
        final OffsetDateTime endsAt = OffsetDateTime.now().plusDays(5);

        final PurchaseOrderEntity target = PurchaseOrderEntity.builder().id(id).tenantId(tenantId).currency("USD")
                .extra(originalExtra).startsAt(startsAt).endsAt(endsAt).createdAt(createdAt).updatedAt(updatedAt)
                .build();

        final Map<String, Object> newExtra = new HashMap<>();
        newExtra.put("source", "api");
        newExtra.put("promoCode", "WINTER2025");

        final PurchaseOrderUpdateRequest source = new PurchaseOrderUpdateRequest(newExtra);

        mapper.updateEntity(source, target);

        assertEquals(newExtra, target.getExtra());

        assertEquals(id, target.getId());
        assertEquals(tenantId, target.getTenantId());
        assertEquals("USD", target.getCurrency());
        assertEquals(createdAt, target.getCreatedAt());
        assertEquals(updatedAt, target.getUpdatedAt());
        assertEquals(startsAt, target.getStartsAt());
        assertEquals(endsAt, target.getEndsAt());
    }

    @Test
    void toDtoAllFieldsMapped() {
        final UUID id = UUID.randomUUID();
        final OffsetDateTime createdAt = OffsetDateTime.now().minusDays(2);
        final OffsetDateTime updatedAt = OffsetDateTime.now().minusDays(1);
        final OffsetDateTime startsAt = OffsetDateTime.now().plusDays(1);
        final OffsetDateTime endsAt = OffsetDateTime.now().plusDays(7);

        final Map<String, Object> extra = new HashMap<>();
        extra.put("source", "web_portal");

        final PurchaseOrderItemEntity item = PurchaseOrderItemEntity.builder().price(1500L).quantity(2).build();

        final PurchaseOrderEntity entity = PurchaseOrderEntity.builder().id(id).tenantId("tenant-1").currency("USD")
                .extra(extra).startsAt(startsAt).endsAt(endsAt).items(List.of(item)).createdAt(createdAt)
                .updatedAt(updatedAt).build();

        final AmountCalculator.Amounts expectedTotals = AmountCalculator.calculate(entity);

        final PurchaseOrder dto = mapper.toDto(entity);

        assertNotNull(dto);

        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getStartsAt(), dto.getStartsAt());
        assertEquals(entity.getEndsAt(), dto.getEndsAt());
        assertEquals(entity.getExtra(), dto.getExtra());
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());

        assertEquals(expectedTotals.netAmount(), dto.getNetAmount());
        assertEquals(expectedTotals.taxAmount(), dto.getTaxAmount());
        assertEquals(expectedTotals.grossAmount(), dto.getGrossAmount());
    }

    @Test
    void toPageSingleItem() {
        final PurchaseOrderEntity e = new PurchaseOrderEntity();
        final Page<PurchaseOrderEntity> page = new PageImpl<>(List.of(e), PageRequest.of(2, 5), 17);

        final PurchaseOrderPage result = mapper.toPage(page);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());

        assertEquals(2, result.getPage());
        assertEquals(5, result.getPageSize());
        assertEquals(17, result.getTotalItems());
        assertEquals(page.getTotalPages(), result.getTotalPages());
        assertEquals(page.hasPrevious(), result.getHasPrev());
        assertEquals(page.hasNext(), result.getHasNext());
    }

    @Test
    void toPageEmpty() {
        final Page<PurchaseOrderEntity> emptyPage = Page.empty(PageRequest.of(0, 10));

        final PurchaseOrderPage result = mapper.toPage(emptyPage);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(0, result.getTotalItems());
        assertEquals(0, result.getTotalPages());
        assertFalse(result.getHasPrev());
        assertFalse(result.getHasNext());
    }
}
