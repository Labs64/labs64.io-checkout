package io.labs64.checkout.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.ShippingInfoEntity;
import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.ConsentRequiredException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.ShippingInfoMessages;
import io.labs64.checkout.repository.ShippingInfoRepository;
import io.labs64.checkout.repository.ShippingRepository;
import io.labs64.checkout.service.ShippingInfoService;

@ExtendWith(MockitoExtension.class)
class ShippingInfoServiceTest {

    @Mock
    ShippingInfoRepository repository;
    @Mock
    ShippingRepository shippingRepository;
    @Mock
    ShippingInfoMessages msg;

    @InjectMocks
    ShippingInfoService service;

    @Test
    void shouldReturnOptionalWhenFind() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final ShippingInfoEntity e = new ShippingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(e));

        final Optional<ShippingInfoEntity> result = service.find(tenant, id);

        assertThat(result).contains(e);
        verify(repository).findByIdAndTenantId(id, tenant);
    }

    @Test
    void shouldReturnEntityWhenGetFound() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final ShippingInfoEntity e = new ShippingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(e));

        final ShippingInfoEntity result = service.get(tenant, id);

        assertThat(result).isSameAs(e);
    }

    @Test
    void shouldThrowNotFoundWhenGetMissing() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn("not found");

        assertThatThrownBy(() -> service.get(tenant, id)).isInstanceOf(NotFoundException.class).hasMessage("not found");
    }

    @Test
    void shouldListByTenantWhenQueryBlankOrNull() {
        final String tenant = "t1";
        final Pageable pageable = Pageable.unpaged();
        final Page<ShippingInfoEntity> expected = new PageImpl<>(List.of(new ShippingInfoEntity()));
        when(repository.findByTenantId(tenant, pageable)).thenReturn(expected);

        assertThat(service.list(tenant, "   ", pageable)).isSameAs(expected);
        assertThat(service.list(tenant, null, pageable)).isSameAs(expected);
        verify(repository, times(2)).findByTenantId(tenant, pageable);
    }

    @Test
    void shouldThrowConsentRequiredWhenCreateWithoutConfirmedAt() {
        final String tenant = "t1";
        final ShippingInfoEntity entity = new ShippingInfoEntity();
        when(msg.consentRequired()).thenReturn("consent required");

        assertThatThrownBy(() -> service.create(tenant, entity)).isInstanceOf(ConsentRequiredException.class)
                .hasMessage("consent required");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldSetTenantAndSaveWhenConfirmedAtPresent() {
        final String tenant = "t1";
        final ShippingInfoEntity entity = new ShippingInfoEntity();
        entity.setConfirmedAt(OffsetDateTime.now());

        when(repository.save(any(ShippingInfoEntity.class)))
                .thenAnswer((Answer<ShippingInfoEntity>) inv -> inv.getArgument(0));

        final ShippingInfoEntity saved = service.create(tenant, entity);

        assertThat(saved).isSameAs(entity);
        assertThat(saved.getTenantId()).isEqualTo(tenant);
        verify(repository).save(entity);
    }

    @Test
    void shouldPersistWhenConfirmedIsTrue() {
        final String tenant = "t1";
        final ShippingInfoEntity entity = new ShippingInfoEntity();

        when(repository.save(any(ShippingInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        final ShippingInfoEntity saved = service.create(tenant, entity, true);

        assertThat(saved.getTenantId()).isEqualTo(tenant);
        assertThat(saved.getConfirmedAt()).isNotNull();
        verify(repository).save(saved);
    }

    @Test
    void shouldApplyUpdaterWhenUpdateFound() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final ShippingInfoEntity entity = new ShippingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        final AtomicBoolean called = new AtomicBoolean(false);
        final ShippingInfoEntity result = service.update(tenant, id, (b) -> called.set(true));

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

        assertThatThrownBy(() -> service.update(tenant, id, b -> {
        })).isInstanceOf(NotFoundException.class).hasMessage("not found");
    }

    @Test
    void shouldThrowConflictWhenDeleteLinked() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(shippingRepository.existsByTenantIdAndShippingInfoId(tenant, id)).thenReturn(true);
        when(msg.deleteConflict(id)).thenReturn("linked");

        assertThatThrownBy(() -> service.delete(tenant, id)).isInstanceOf(ConflictException.class).hasMessage("linked");

        verify(repository, never()).deleteByIdAndTenantId(any(), anyString());
    }

    @Test
    void shouldReturnTrueWhenDeleteAffectedGtZero() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(shippingRepository.existsByTenantIdAndShippingInfoId(tenant, id)).thenReturn(false);
        when(repository.deleteByIdAndTenantId(id, tenant)).thenReturn(1);

        assertThat(service.delete(tenant, id)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenDeleteAffectedZero() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(shippingRepository.existsByTenantIdAndShippingInfoId(tenant, id)).thenReturn(false);
        when(repository.deleteByIdAndTenantId(id, tenant)).thenReturn(0);

        assertThat(service.delete(tenant, id)).isFalse();
    }
}
