package io.labs64.checkout.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.entity.CheckoutIntentEntity;
import io.labs64.checkout.exception.ConflictException;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.CheckoutIntentMessages;
import io.labs64.checkout.model.CheckoutIntentStatus;
import io.labs64.checkout.repository.CheckoutIntentRepository;
import io.labs64.checkout.rules.CheckoutIntentStatusRules;
import io.labs64.checkout.service.CheckoutIntentService;

@ExtendWith(MockitoExtension.class)
class CheckoutIntentServiceTest {

    @Mock
    CheckoutIntentRepository repository;
    @Mock
    CheckoutIntentMessages msg;

    @InjectMocks
    CheckoutIntentService service;

    @Test
    void shouldReturnOptionalWhenFind() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final CheckoutIntentEntity e = new CheckoutIntentEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(e));

        final Optional<CheckoutIntentEntity> result = service.find(tenant, id);

        assertThat(result).contains(e);
        verify(repository).findByIdAndTenantId(id, tenant);
    }

    @Test
    void shouldReturnEntityWhenGetFound() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final CheckoutIntentEntity e = new CheckoutIntentEntity();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(e));

        final CheckoutIntentEntity result = service.get(tenant, id);

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
    void shouldListByTenant() {
        final String tenant = "t1";
        final Pageable pageable = Pageable.unpaged();
        final Page<CheckoutIntentEntity> expected = new PageImpl<>(List.of(new CheckoutIntentEntity()));
        when(repository.findByTenantId(tenant, pageable)).thenReturn(expected);

        assertThat(service.list(tenant, "   ", pageable)).isSameAs(expected);
        assertThat(service.list(tenant, null, pageable)).isSameAs(expected);

        verify(repository, times(2)).findByTenantId(tenant, pageable);
    }

    @Test
    void shouldCreate() {
        final String tenant = "t1";
        final CheckoutIntentEntity entity = new CheckoutIntentEntity();

        when(repository.save(any(CheckoutIntentEntity.class)))
                .thenAnswer((Answer<CheckoutIntentEntity>) inv -> inv.getArgument(0));

        final CheckoutIntentEntity saved = service.create(tenant, entity);

        assertThat(saved).isSameAs(entity);
        assertThat(saved.getTenantId()).isEqualTo(tenant);
        verify(repository).save(entity);
    }

    @Test
    void shouldUpdateWhenFoundAndNotFinished() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final CheckoutIntentEntity entity = new CheckoutIntentEntity();
        entity.setStatus(CheckoutIntentStatus.PENDING); // не finished
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));

        try (final MockedStatic<CheckoutIntentStatusRules> mocked = mockStatic(CheckoutIntentStatusRules.class)) {
            mocked.when(CheckoutIntentStatusRules::finishedStatuses).thenReturn(Set.of(CheckoutIntentStatus.COMPLETED)); // PENDING
                                                                                                                         // не
                                                                                                                         // в
                                                                                                                         // сеті

            final AtomicBoolean called = new AtomicBoolean(false);
            final CheckoutIntentEntity result = service.update(tenant, id, (ci) -> called.set(true));

            assertThat(called).isTrue();
            assertThat(result).isSameAs(entity);
        }
    }

    @Test
    void shouldThrowConflictWhenUpdateFinishedStatus() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final CheckoutIntentEntity entity = new CheckoutIntentEntity();
        entity.setStatus(CheckoutIntentStatus.COMPLETED);
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.of(entity));
        when(msg.cannotUpdateFinished(id, CheckoutIntentStatus.COMPLETED)).thenReturn("finished");

        try (final MockedStatic<CheckoutIntentStatusRules> mocked = mockStatic(CheckoutIntentStatusRules.class)) {
            mocked.when(CheckoutIntentStatusRules::finishedStatuses).thenReturn(Set.of(CheckoutIntentStatus.COMPLETED));

            assertThatThrownBy(() -> service.update(tenant, id, ci -> {
            })).isInstanceOf(ConflictException.class).hasMessage("finished");
        }
    }

    @Test
    void shouldThrowNotFoundWhenUpdateMissing() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(repository.findByIdAndTenantId(id, tenant)).thenReturn(Optional.empty());
        when(msg.notFound(id)).thenReturn("not found");

        assertThatThrownBy(() -> service.update(tenant, id, ci -> {
        })).isInstanceOf(NotFoundException.class).hasMessage("not found");
    }
}
