package io.labs64.checkout.v1.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.ConsentRequiredException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.BillingInfoMessages;
import io.labs64.checkout.rules.BillingInfoLockedSnapshot;
import io.labs64.checkout.v1.entity.BillingInfoEntity;
import io.labs64.checkout.v1.repository.BillingInfoRepository;
import io.labs64.checkout.v1.repository.CheckoutIntentRepository;

@ExtendWith(MockitoExtension.class)
class BillingInfoServiceTest {

    @Mock
    BillingInfoRepository repository;
    @Mock
    CheckoutIntentRepository checkoutIntentRepository;
    @Mock
    BillingInfoMessages msg;

    @InjectMocks
    BillingInfoService service;

    @Test
    void shouldReturnOptionalWhenFind() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoEntity entity = new BillingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        final Optional<BillingInfoEntity> result = service.find(tenant, id);

        assertThat(result).contains(entity);
        verify(repository).findByIdAndTenantId(id, tenant);
    }

    @Test
    void shouldReturnEntityWhenGetFound() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoEntity entity = new BillingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        final BillingInfoEntity result = service.get(tenant, id);

        assertThat(result).isSameAs(entity);
    }

    @Test
    void shouldThrowNotFoundWhenGetMissing() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.empty());
        when(msg.notFoundById(id)).thenReturn("not found");

        assertThatThrownBy(() -> service.get(tenant, id)).isInstanceOf(NotFoundException.class).hasMessage("not found");
    }

    @Test
    void shouldThrowConsentRequiredWhenCreateWithoutConfirmed() {
        final String tenant = "t1";
        final BillingInfoEntity entity = new BillingInfoEntity(); // confirmedAt == null
        when(msg.consentRequired()).thenReturn("consent required");

        assertThatThrownBy(() -> service.create(tenant, entity)).isInstanceOf(ConsentRequiredException.class)
                .hasMessage("consent required");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldPersistWhenConfirmedAtIsSet() {
        final String tenant = "t1";
        final BillingInfoEntity entity = new BillingInfoEntity();
        entity.setConfirmedAt(OffsetDateTime.now());

        when(repository.save(any(BillingInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        final BillingInfoEntity saved = service.create(tenant, entity);

        assertThat(saved).isSameAs(entity);
        assertThat(saved.getTenantId()).isEqualTo(tenant);
        verify(repository).save(entity);
    }

    @Test
    void shouldSetConfirmedTimestampWhenCreateWithConfirmedFlag() {
        final String tenant = "t1";
        final BillingInfoEntity entity = new BillingInfoEntity(); // confirmedAt == null
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final BillingInfoEntity saved = service.create(tenant, entity, true);

        assertThat(saved.getConfirmedAt()).isNotNull();
        assertThat(saved.getTenantId()).isEqualTo(tenant);
    }

    @Test
    void shouldThrowConsentRequiredWhenCreateWithConfirmedFalse() {
        final String tenant = "t1";
        final BillingInfoEntity entity = new BillingInfoEntity(); // confirmedAt == null
        when(msg.consentRequired()).thenReturn("consent required");

        assertThatThrownBy(() -> service.create(tenant, entity, false)).isInstanceOf(ConsentRequiredException.class)
                .hasMessage("consent required");
    }

    // ---------- update ----------

    @Test
    void shouldApplyUpdaterWhenUnlocked() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoEntity entity = new BillingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));
        when(checkoutIntentRepository.existsByTenantIdAndBillingInfoIdAndStatusIn(eq(tenant), eq(id), any()))
                .thenReturn(false);

        final AtomicBoolean called = new AtomicBoolean(false);
        final BillingInfoEntity result = service.update(tenant, id, (b) -> called.set(true));

        assertThat(called).isTrue();
        assertThat(result).isSameAs(entity);
    }

    @Test
    void shouldAllowUpdateWhenLockedButSnapshotUnchanged() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoEntity entity = new BillingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));
        when(checkoutIntentRepository.existsByTenantIdAndBillingInfoIdAndStatusIn(eq(tenant), eq(id), any()))
                .thenReturn(true);

        try (final MockedStatic<BillingInfoLockedSnapshot> mocked = mockStatic(BillingInfoLockedSnapshot.class)) {
            final BillingInfoLockedSnapshot snapshot = mock(BillingInfoLockedSnapshot.class);
            // return the "same" snapshot before and after — equals -> true
            mocked.when(() -> BillingInfoLockedSnapshot.from(any(BillingInfoEntity.class))).thenReturn(snapshot,
                    snapshot);

            final AtomicBoolean called = new AtomicBoolean(false);
            final BillingInfoEntity result = service.update(tenant, id, (b) -> called.set(true));

            assertThat(called).isTrue();
            assertThat(result).isSameAs(entity);
        }
    }

    @Test
    void shouldThrowConflictWhenLockedAndSnapshotChanges() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoEntity entity = new BillingInfoEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));
        when(checkoutIntentRepository.existsByTenantIdAndBillingInfoIdAndStatusIn(eq(tenant), eq(id), any()))
                .thenReturn(true);
        when(msg.lockedByTerminalCheckoutIntent(id)).thenReturn("locked");

        try (final MockedStatic<BillingInfoLockedSnapshot> mocked = mockStatic(BillingInfoLockedSnapshot.class)) {
            // two different mocks -> equals will return false (different references)
            mocked.when(() -> BillingInfoLockedSnapshot.from(any(BillingInfoEntity.class)))
                    .thenReturn(mock(BillingInfoLockedSnapshot.class), mock(BillingInfoLockedSnapshot.class));

            assertThatThrownBy(() -> service.update(tenant, id, (b) -> {
                /* mutate */ })).isInstanceOf(ConflictException.class).hasMessage("locked");
        }
    }

    @Test
    void shouldThrowNotFoundWhenEntityMissing() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.empty());
        when(msg.notFoundById(id)).thenReturn("not found");

        assertThatThrownBy(() -> service.update(tenant, id, b -> {
        })).isInstanceOf(NotFoundException.class).hasMessage("not found");
    }

    @Test
    void shouldThrowConflictWhenDeleteLinked() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(checkoutIntentRepository.existsByTenantIdAndBillingInfoId(tenant, id)).thenReturn(true);
        when(msg.deleteConflictLinkedToCheckoutIntents(id)).thenReturn("linked");

        assertThatThrownBy(() -> service.delete(tenant, id)).isInstanceOf(ConflictException.class).hasMessage("linked");
        verify(repository, never()).deleteByIdAndTenantId(any(), anyString());
    }

    @Test
    void shouldReturnTrueWhenDeleteAffectedGtZero() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(checkoutIntentRepository.existsByTenantIdAndBillingInfoId(tenant, id)).thenReturn(false);
        when(repository.deleteByIdAndTenantId(id, tenant)).thenReturn(1);

        assertThat(service.delete(tenant, id)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenDeleteAffectedZero() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(checkoutIntentRepository.existsByTenantIdAndBillingInfoId(tenant, id)).thenReturn(false);
        when(repository.deleteByIdAndTenantId(id, tenant)).thenReturn(0);

        assertThat(service.delete(tenant, id)).isFalse();
    }
}
