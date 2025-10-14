package io.labs64.checkout.v1.controller;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.v1.entity.BillingInfoEntity;
import io.labs64.checkout.v1.entity.CheckoutIntentEntity;
import io.labs64.checkout.v1.mapper.CheckoutIntentMapper;
import io.labs64.checkout.v1.model.CheckoutIntent;
import io.labs64.checkout.v1.model.CheckoutIntentCreateRequest;
import io.labs64.checkout.v1.model.CheckoutIntentPage;
import io.labs64.checkout.v1.model.CheckoutIntentUpdateRequest;
import io.labs64.checkout.v1.service.BillingInfoService;
import io.labs64.checkout.v1.service.CheckoutIntentService;
import io.labs64.checkout.v1.web.tenant.RequestTenantProvider;

class CheckoutIntentControllerTest {

    private final RequestTenantProvider tenantProvider = mock(RequestTenantProvider.class);
    private final CheckoutIntentService ciService = mock(CheckoutIntentService.class);
    private final BillingInfoService biService = mock(BillingInfoService.class);
    private final CheckoutIntentMapper mapper = mock(CheckoutIntentMapper.class);

    private final CheckoutIntentController controller = new CheckoutIntentController(tenantProvider, ciService,
            biService, mapper);

    @Test
    void shouldReturnOkAndBodyWhenGetCheckoutIntent() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final CheckoutIntentEntity entity = new CheckoutIntentEntity();
        final CheckoutIntent dto = new CheckoutIntent();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(ciService.get(tenant, id)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<CheckoutIntent> resp = controller.getCheckoutIntent(id);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(ciService).get(tenant, id);
        verify(mapper).toDto(entity);
    }

    @Test
    void shouldReturnOkAndPageWhenListCheckoutIntents() {
        final String tenant = "t1";
        final String query = "status:pending";
        final Pageable pageable = Pageable.unpaged();

        final Page<CheckoutIntentEntity> page = new PageImpl<>(List.of(new CheckoutIntentEntity()));
        final CheckoutIntentPage pageDto = new CheckoutIntentPage();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(ciService.list(tenant, query, pageable)).thenReturn(page);
        when(mapper.mapToDto(page)).thenReturn(pageDto);

        final ResponseEntity<CheckoutIntentPage> resp = controller.listCheckoutIntents(query, pageable);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(pageDto);
        verify(ciService).list(tenant, query, pageable);
        verify(mapper).mapToDto(page);
    }

    @Test
    void shouldReturnCreatedAndBodyWhenCreateWithoutBillingInfo() {
        final String tenant = "t1";
        final CheckoutIntentCreateRequest req = new CheckoutIntentCreateRequest().billingInfoId(null);

        final CheckoutIntentEntity toCreate = new CheckoutIntentEntity();
        final CheckoutIntentEntity saved = new CheckoutIntentEntity();
        final CheckoutIntent dto = new CheckoutIntent();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(mapper.toEntity(req)).thenReturn(toCreate);
        when(ciService.create(tenant, toCreate)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        final ResponseEntity<CheckoutIntent> resp = controller.createCheckoutIntent(req);

        assertThat(resp.getStatusCode().value()).isEqualTo(201);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(biService, never()).get(anyString(), any());
        verify(ciService).create(tenant, toCreate);
    }

    @Test
    void shouldSetBillingInfoAndCreateWhenBillingInfoIdProvided() {
        final String tenant = "t1";
        final UUID billingInfoId = UUID.randomUUID();
        final CheckoutIntentCreateRequest req = new CheckoutIntentCreateRequest().billingInfoId(billingInfoId);

        final CheckoutIntentEntity toCreate = new CheckoutIntentEntity();
        final BillingInfoEntity billingInfo = new BillingInfoEntity();
        final CheckoutIntentEntity saved = new CheckoutIntentEntity();
        final CheckoutIntent dto = new CheckoutIntent();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(mapper.toEntity(req)).thenReturn(toCreate);
        when(biService.get(tenant, billingInfoId)).thenReturn(billingInfo);
        when(ciService.create(tenant, toCreate)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        final ResponseEntity<CheckoutIntent> resp = controller.createCheckoutIntent(req);

        assertThat(resp.getStatusCode().value()).isEqualTo(201);
        assertThat(resp.getBody()).isSameAs(dto);
        assertThat(toCreate.getBillingInfo()).isSameAs(billingInfo);
        verify(biService).get(tenant, billingInfoId);
        verify(ciService).create(tenant, toCreate);
    }

    @Test
    void shouldApplyMapperAndReturnOkWhenUpdateWithoutBillingInfo() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();

        final CheckoutIntentUpdateRequest req = new CheckoutIntentUpdateRequest().billingInfoId(null);
        final CheckoutIntentEntity entity = new CheckoutIntentEntity();
        final CheckoutIntent dto = new CheckoutIntent();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(ciService.update(eq(tenant), eq(id), any())).thenAnswer(inv -> {
            final Consumer<CheckoutIntentEntity> updater = inv.getArgument(2);
            updater.accept(entity);
            return entity;
        });
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<CheckoutIntent> resp = controller.updateCheckoutIntent(id, req);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(mapper).updateEntity(req, entity);
        verify(biService, never()).get(anyString(), any());
        verify(ciService).update(eq(tenant), eq(id), any());
    }

    @Test
    void shouldSetBillingInfoInUpdaterWhenBillingInfoIdProvided() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final UUID billingInfoId = UUID.randomUUID();

        final CheckoutIntentUpdateRequest req = new CheckoutIntentUpdateRequest().billingInfoId(billingInfoId);
        final CheckoutIntentEntity entity = new CheckoutIntentEntity();
        final BillingInfoEntity billingInfo = new BillingInfoEntity();
        final CheckoutIntent dto = new CheckoutIntent();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(biService.get(tenant, billingInfoId)).thenReturn(billingInfo);
        when(ciService.update(eq(tenant), eq(id), any())).thenAnswer(inv -> {
            final Consumer<CheckoutIntentEntity> updater = inv.getArgument(2);
            updater.accept(entity);
            return entity;
        });
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<CheckoutIntent> resp = controller.updateCheckoutIntent(id, req);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(mapper).updateEntity(req, entity);
        assertThat(entity.getBillingInfo()).isSameAs(billingInfo);
        verify(biService).get(tenant, billingInfoId);
        verify(ciService).update(eq(tenant), eq(id), any());
    }
}
