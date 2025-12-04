package io.labs64.checkout.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.labs64.checkout.entity.PurchaseOrderItemEntity;
import io.labs64.checkout.model.PurchaseOrderItem;
import io.labs64.checkout.model.Tax;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderItemMapperTest {

    @Mock
    private UriMapper uriMapper;

    @InjectMocks
    private io.labs64.checkout.mapper.PurchaseOrderItemMapperImpl mapper;

    @Test
    void toEntityAllFieldsMapped() {
        final Tax tax = new Tax();
        final Map<String, Object> extra = new HashMap<>();
        extra.put("source", "web");
        extra.put("promoCode", "SUMMER2025");

        final PurchaseOrderItem source = new PurchaseOrderItem("Pro plan", 1999L, 3);
        source.setDescription("Pro subscription");
        source.setUrl(URI.create("https://example.com/product/pro"));
        source.setImage(URI.create("https://example.com/product/pro.png"));
        source.setSku("PRO-001");
        source.setUom("PCS");
        source.setTax(tax);
        source.setExtra(extra);

        final PurchaseOrderItemEntity result = mapper.toEntity(source);

        assertNotNull(result);

        assertEquals(source.getName(), result.getName());
        assertEquals(source.getDescription(), result.getDescription());
        assertEquals(source.getSku(), result.getSku());
        assertEquals(source.getUom(), result.getUom());
        assertEquals(source.getPrice(), result.getPrice());
        assertEquals(source.getQuantity(), result.getQuantity());

        assertSame(tax, result.getTax());

        assertNotNull(result.getExtra());
        assertEquals(extra, result.getExtra());

        assertNull(result.getId());
        assertNull(result.getPurchaseOrder());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void toDtoAllFieldsMapped() {
        final Tax tax = new Tax();
        final Map<String, Object> extra = new HashMap<>();
        extra.put("source", "api");
        extra.put("promoCode", "WINTER2025");

        final PurchaseOrderItemEntity entity = PurchaseOrderItemEntity.builder()
                .name("Basic plan")
                .description("Basic subscription")
                .url("https://example.com/product/basic")
                .image("https://example.com/product/basic.png")
                .sku("BASIC-001")
                .uom("PCS")
                .price(999L)
                .quantity(1)
                .tax(tax)
                .extra(extra)
                .build();

        final PurchaseOrderItem dto = mapper.toDto(entity);

        assertNotNull(dto);

        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getSku(), dto.getSku());
        assertEquals(entity.getUom(), dto.getUom());
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getQuantity(), dto.getQuantity());

        assertSame(tax, dto.getTax());

        assertNotNull(dto.getExtra());
        assertEquals(extra, dto.getExtra());
    }

    @Test
    void toEntityList() {
        final PurchaseOrderItem item1 = new PurchaseOrderItem("Item 1", 100L, 1);
        final PurchaseOrderItem item2 = new PurchaseOrderItem("Item 2", 200L, 2);

        final List<PurchaseOrderItemEntity> entities = mapper.toEntity(List.of(item1, item2));

        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("Item 1", entities.get(0).getName());
        assertEquals("Item 2", entities.get(1).getName());
    }

    @Test
    void toDtoList() {
        final PurchaseOrderItemEntity e1 = PurchaseOrderItemEntity.builder()
                .name("Item 1")
                .price(100L)
                .quantity(1)
                .build();
        final PurchaseOrderItemEntity e2 = PurchaseOrderItemEntity.builder()
                .name("Item 2")
                .price(200L)
                .quantity(2)
                .build();

        final List<PurchaseOrderItem> dtos = mapper.toDto(List.of(e1, e2));

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Item 1", dtos.get(0).getName());
        assertEquals("Item 2", dtos.get(1).getName());
    }
}
