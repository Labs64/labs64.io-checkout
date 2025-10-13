package io.labs64.checkout.v1.service;

import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.ShippingMessages;
import io.labs64.checkout.v1.entity.CheckoutIntentEntity;
import io.labs64.checkout.v1.entity.ShippingEntity;
import io.labs64.checkout.v1.entity.ShippingInfoEntity;
import io.labs64.checkout.v1.repository.CheckoutIntentRepository;
import io.labs64.checkout.v1.repository.ShippingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock ShippingRepository repository;
    @Mock ShippingInfoService shippingInfoService;
    @Mock CheckoutIntentService checkoutIntentService;
    @Mock CheckoutIntentRepository checkoutIntentRepository;
    @Mock ShippingMessages msg;

    @InjectMocks ShippingService service;
    @Test
    void shouldReturnOptionalWhenFind() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final ShippingEntity entity = new ShippingEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        final Optional<ShippingEntity> result = service.find(tenant, id);

        assertThat(result).contains(entity);
        verify(repository).findByIdAndTenantId(id, tenant);
    }

    @Test
    void shouldReturnEntityWhenGetFound() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final ShippingEntity entity = new ShippingEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        final ShippingEntity result = service.get(tenant, id);

        assertThat(result).isSameAs(entity);
    }

    @Test
    void shouldThrowNotFoundWhenGetMissing() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn("not found");

        assertThatThrownBy(() -> service.get(tenant, id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldListByTenantWhenQueryBlankOrNull() {
        final String tenant = "t1";
        final Pageable pageable = Pageable.unpaged();
        final Page<ShippingEntity> expected = new PageImpl<>(List.of(new ShippingEntity()));
        when(repository.findByTenantId(tenant, pageable)).thenReturn(expected);

        assertThat(service.list(tenant, "   ", pageable)).isSameAs(expected);
        assertThat(service.list(tenant, null, pageable)).isSameAs(expected);

        verify(repository, times(2)).findByTenantId(tenant, pageable);
    }

    @Test
    void shouldSetTenantLinkEntitiesAndSaveWhenCreate() {
        final String tenant = "t1";
        final UUID shippingInfoId = UUID.randomUUID();
        final UUID checkoutIntentId = UUID.randomUUID();

        final ShippingEntity toSave = new ShippingEntity();
        final ShippingInfoEntity shippingInfo = new ShippingInfoEntity();
        final CheckoutIntentEntity checkoutIntent = new CheckoutIntentEntity();

        when(shippingInfoService.get(tenant, shippingInfoId)).thenReturn(shippingInfo);
        when(checkoutIntentService.get(tenant, checkoutIntentId)).thenReturn(checkoutIntent);
        when(repository.save(any(ShippingEntity.class)))
                .thenAnswer((Answer<ShippingEntity>) inv -> inv.getArgument(0));

        final ShippingEntity saved = service.create(tenant, shippingInfoId, checkoutIntentId, toSave);

        assertThat(saved).isSameAs(toSave);
        assertThat(saved.getTenantId()).isEqualTo(tenant);
        assertThat(saved.getShippingInfo()).isSameAs(shippingInfo);
        assertThat(saved.getCheckoutIntent()).isSameAs(checkoutIntent);

        verify(shippingInfoService).get(tenant, shippingInfoId);
        verify(checkoutIntentService).get(tenant, checkoutIntentId);
        verify(repository).save(toSave);
    }

    @Test
    void shouldApplyUpdaterWhenUpdateFound() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final ShippingEntity entity = new ShippingEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        final AtomicBoolean called = new AtomicBoolean(false);
        final ShippingEntity result = service.update(tenant, id, s -> called.set(true));

        assertThat(called).isTrue();
        assertThat(result).isSameAs(entity);
        verify(repository).findByIdAndTenantId(id, tenant);
    }

    @Test
    void shouldThrowNotFoundWhenUpdateMissing() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn("not found");

        assertThatThrownBy(() -> service.update(tenant, id, s -> {}))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("not found");
    }


    @Test
    void shouldThrowConflictWhenDeleteLinked() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(checkoutIntentRepository.existsByTenantIdAndShippingId(tenant, id)).thenReturn(true);
        when(msg.deleteConflict(id)).thenReturn("linked");

        assertThatThrownBy(() -> service.delete(tenant, id))
                .isInstanceOf(ConflictException.class)
                .hasMessage("linked");

        verify(repository, never()).deleteByIdAndTenantId(any(), anyString());
    }

    @Test
    void shouldReturnTrueWhenDeleteAffectedGtZero() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(checkoutIntentRepository.existsByTenantIdAndShippingId(tenant, id)).thenReturn(false);
        when(repository.deleteByIdAndTenantId(id, tenant)).thenReturn(1);

        assertThat(service.delete(tenant, id)).isTrue();
        verify(repository).deleteByIdAndTenantId(id, tenant);
    }

    @Test
    void shouldReturnFalseWhenDeleteAffectedZero() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(checkoutIntentRepository.existsByTenantIdAndShippingId(tenant, id)).thenReturn(false);
        when(repository.deleteByIdAndTenantId(id, tenant)).thenReturn(0);

        assertThat(service.delete(tenant, id)).isFalse();
        verify(repository).deleteByIdAndTenantId(id, tenant);
    }
}
