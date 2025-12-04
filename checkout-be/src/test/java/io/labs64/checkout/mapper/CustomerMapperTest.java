package io.labs64.checkout.mapper;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.Customer;
import io.labs64.checkout.model.CustomerCreateRequest;
import io.labs64.checkout.model.CustomerPage;
import io.labs64.checkout.model.CustomerUpdateRequest;
import io.labs64.checkout.model.ShippingInfo;

class CustomerMapperTest {

    private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void toEntityAllFieldsMapped() {
        final BillingInfo billingInfo = new BillingInfo();
        billingInfo.setFirstName("Don");
        billingInfo.setLastName("Joe");

        final ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setCountry("US");
        shippingInfo.setState("NY");
        shippingInfo.setCity("New York");
        shippingInfo.setPostalCode("9410");

        final Map<String, Object> extra = new HashMap<>();
        extra.put("someKey", "someValue");

        final CustomerCreateRequest source = new CustomerCreateRequest("Doe");
        source.setFirstName("John");
        source.setLastName("Doe");
        source.setEmail("john.doe@example.com");
        source.setPhone("+1234567890");
        source.setBillingInfo(billingInfo);
        source.setShippingInfo(shippingInfo);
        source.setExtra(extra);

        final CustomerEntity result = mapper.toEntity(source);

        assertNotNull(result);

        assertEquals(source.getFirstName(), result.getFirstName());
        assertEquals(source.getLastName(), result.getLastName());
        assertEquals(source.getEmail(), result.getEmail());
        assertEquals(source.getPhone(), result.getPhone());
        assertSame(billingInfo, result.getBillingInfo());
        assertSame(shippingInfo, result.getShippingInfo());
        assertEquals(extra, result.getExtra());

        assertNull(result.getId());
        assertNull(result.getTenantId());
    }

    @Test
    void updateEntityAllFieldsMapped() {
        final UUID id = UUID.randomUUID();
        final String tenantId = "tenant-1";
        final OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        final OffsetDateTime updatedAt = OffsetDateTime.now().minusHours(1);

        final BillingInfo oldBilling = new BillingInfo();
        final ShippingInfo oldShipping = new ShippingInfo();

        final CustomerEntity target = CustomerEntity.builder()
                .id(id)
                .tenantId(tenantId)
                .firstName("OldFirst")
                .lastName("OldLast")
                .email("old@example.com")
                .phone("+111")
                .billingInfo(oldBilling)
                .shippingInfo(oldShipping)
                .build();
        target.setCreatedAt(createdAt);
        target.setUpdatedAt(updatedAt);

        final BillingInfo newBilling = new BillingInfo();
        newBilling.setFirstName("Don");
        newBilling.setLastName("Joe");

        final ShippingInfo newShipping = new ShippingInfo();
        newShipping.setCountry("US");
        newShipping.setState("NY");
        newShipping.setCity("New York");
        newShipping.setPostalCode("9410");

        final Map<String, Object> newExtra = new HashMap<>();
        newExtra.put("someKey", "someValue");

        final CustomerUpdateRequest source = new CustomerUpdateRequest();
        source.setFirstName("NewFirst");
        source.setLastName("NewLast");
        source.setEmail("new@example.com");
        source.setPhone("+222");
        source.setBillingInfo(newBilling);
        source.setShippingInfo(newShipping);
        source.setExtra(newExtra);

        mapper.updateEntity(source, target);

        assertEquals(source.getFirstName(), target.getFirstName());
        assertEquals(source.getLastName(), target.getLastName());
        assertEquals(source.getEmail(), target.getEmail());
        assertEquals(source.getPhone(), target.getPhone());
        assertEquals(source.getExtra(), target.getExtra());
        assertSame(newBilling, target.getBillingInfo());
        assertSame(newShipping, target.getShippingInfo());

        assertEquals(id, target.getId());
        assertEquals(tenantId, target.getTenantId());
        assertEquals(createdAt, target.getCreatedAt());
        assertEquals(updatedAt, target.getUpdatedAt());
    }

    @Test
    void toDtoAllFieldsMapped() {
        final UUID id = UUID.randomUUID();
        final OffsetDateTime createdAt = OffsetDateTime.now().minusDays(2);
        final OffsetDateTime updatedAt = OffsetDateTime.now().minusDays(1);

        final BillingInfo billingInfo = new BillingInfo();
        billingInfo.setFirstName("Don");
        billingInfo.setLastName("Joe");

        final ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setCountry("US");
        shippingInfo.setState("NY");
        shippingInfo.setCity("New York");
        shippingInfo.setPostalCode("9410");

        final Map<String, Object> extra = new HashMap<>();
        extra.put("someKey", "someValue");

        final CustomerEntity entity = CustomerEntity.builder()
                .id(id)
                .tenantId("tenant-1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .billingInfo(billingInfo)
                .shippingInfo(shippingInfo)
                .extra(extra)
                .build();
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        final Customer dto = mapper.toDto(entity);

        assertNotNull(dto);

        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getPhone(), dto.getPhone());
        assertEquals(entity.getExtra(), dto.getExtra());
        assertSame(billingInfo, dto.getBillingInfo());
        assertSame(shippingInfo, dto.getShippingInfo());

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void toPageSingleItem() {
        // given
        final CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setFirstName("John");
        entity.setLastName("Doe");

        final Page<CustomerEntity> page = new PageImpl<>(
                List.of(entity),
                PageRequest.of(2, 5), // page = 2, size = 5
                17 // total elements
        );

        final CustomerPage result = mapper.toPage(page);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getPage());
        assertEquals(5, result.getPageSize());
        assertEquals(17, result.getTotalItems());
        assertEquals(page.getTotalPages(), result.getTotalPages());
        assertEquals(page.hasPrevious(), result.getHasPrev());
        assertEquals(page.hasNext(), result.getHasNext());

        final Customer first = result.getItems().getFirst();
        assertEquals(entity.getId(), first.getId());
        assertEquals(entity.getFirstName(), first.getFirstName());
        assertEquals(entity.getLastName(), first.getLastName());
    }

    @Test
    void toPageEmpty() {
        final Page<CustomerEntity> emptyPage = Page.empty(PageRequest.of(0, 10));

        final CustomerPage result = mapper.toPage(emptyPage);

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
