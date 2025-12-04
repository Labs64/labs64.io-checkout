package io.labs64.checkout.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
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

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.CheckoutTransaction;
import io.labs64.checkout.model.CheckoutTransactionPage;
import io.labs64.checkout.model.CheckoutTransactionStatus;
import io.labs64.checkout.model.PurchaseOrder;
import io.labs64.checkout.model.ShippingInfo;

@ExtendWith(MockitoExtension.class)
class CheckoutTransactionMapperTest {

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @InjectMocks
    private io.labs64.checkout.mapper.CheckoutTransactionMapperImpl mapper;

    @Test
    void toDtoAllFieldsMapped() {
        // given
        final UUID id = UUID.randomUUID();
        final CheckoutTransactionStatus status = CheckoutTransactionStatus.PENDING;
        final PurchaseOrderEntity purchaseOrderEntity = new PurchaseOrderEntity();
        final BillingInfo billingInfo = new BillingInfo();
        final ShippingInfo shippingInfo = new ShippingInfo();
        final String paymentMethod = "card";
        final OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        final OffsetDateTime updatedAt = OffsetDateTime.now();
        final OffsetDateTime closedAt = OffsetDateTime.now().plusDays(1);

        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        entity.setId(id);
        entity.setTenantId("tenant-1");
        entity.setStatus(status);
        entity.setPurchaseOrder(purchaseOrderEntity);
        entity.setBillingInfo(billingInfo);
        entity.setShippingInfo(shippingInfo);
        entity.setPaymentMethod(paymentMethod);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);
        entity.setClosedAt(closedAt);

        final PurchaseOrder purchaseOrderDto = new PurchaseOrder();
        when(purchaseOrderMapper.toDto(purchaseOrderEntity)).thenReturn(purchaseOrderDto);

        final CheckoutTransaction dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getStatus()).isEqualTo(status);
        assertThat(dto.getPurchaseOrder()).isSameAs(purchaseOrderDto);
        assertThat(dto.getBillingInfo()).isEqualTo(billingInfo);
        assertThat(dto.getShippingInfo()).isEqualTo(shippingInfo);
        assertThat(dto.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(dto.getClosedAt()).isEqualTo(closedAt);
    }

    @Test
    void toPageMapsContentAndMetadata() {
        final CheckoutTransactionEntity entity = new CheckoutTransactionEntity();
        entity.setId(UUID.randomUUID());

        final PageRequest pageable = PageRequest.of(1, 5);
        final Page<CheckoutTransactionEntity> sourcePage =
                new PageImpl<>(List.of(entity), pageable, 13);

        final CheckoutTransactionPage page = mapper.toPage(sourcePage);

        assertThat(page).isNotNull();
        assertThat(page.getItems()).hasSize(1);
        assertThat(page.getItems().getFirst().getId()).isEqualTo(entity.getId());

        assertThat(page.getPage()).isEqualTo(sourcePage.getNumber());
        assertThat(page.getPageSize()).isEqualTo(sourcePage.getSize());
        assertThat(page.getTotalItems()).isEqualTo(sourcePage.getTotalElements());
        assertThat(page.getTotalPages()).isEqualTo(sourcePage.getTotalPages());
        assertThat(page.getHasPrev()).isEqualTo(sourcePage.hasPrevious());
        assertThat(page.getHasNext()).isEqualTo(sourcePage.hasNext());
    }

    @Test
    void toPageEmptyPageMapped() {
        final PageRequest pageable = PageRequest.of(0, 10);
        final Page<CheckoutTransactionEntity> sourcePage = Page.empty(pageable);

        final CheckoutTransactionPage page = mapper.toPage(sourcePage);

        assertThat(page).isNotNull();
        assertThat(page.getItems()).isNotNull().isEmpty();
        assertThat(page.getPage()).isEqualTo(sourcePage.getNumber());
        assertThat(page.getPageSize()).isEqualTo(sourcePage.getSize());
        assertThat(page.getTotalItems()).isEqualTo(sourcePage.getTotalElements());
        assertThat(page.getTotalPages()).isEqualTo(sourcePage.getTotalPages());
        assertThat(page.getHasPrev()).isFalse();
        assertThat(page.getHasNext()).isFalse();
    }
}
