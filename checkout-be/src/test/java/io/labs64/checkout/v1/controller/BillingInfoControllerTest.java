package io.labs64.checkout.controller;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.labs64.checkout.controller.BillingInfoController;
import io.labs64.checkout.entity.BillingInfoEntity;
import io.labs64.checkout.mapper.BillingInfoMapper;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.BillingInfoCreateRequest;
import io.labs64.checkout.model.BillingInfoPage;
import io.labs64.checkout.model.BillingInfoUpdateRequest;
import io.labs64.checkout.service.BillingInfoService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;

class BillingInfoControllerTest {

    private final RequestTenantProvider tenantProvider = mock(RequestTenantProvider.class);
    private final BillingInfoService service = mock(BillingInfoService.class);
    private final BillingInfoMapper mapper = mock(BillingInfoMapper.class);

    private final BillingInfoController controller = new BillingInfoController(tenantProvider, service, mapper);

    @Test
    void shouldReturnOkAndBodyWhenGetBillingInfo() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoEntity entity = new BillingInfoEntity();
        final BillingInfo dto = new BillingInfo();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(service.get(tenant, id)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<BillingInfo> resp = controller.getBillingInfo(id);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(service).get(tenant, id);
        verify(mapper).toDto(entity);
    }

    @Test
    void shouldReturnOkAndPageWhenList() {
        final String tenant = "t1";
        final String query = "email:john";
        final Pageable pageable = Pageable.unpaged();

        final Page<BillingInfoEntity> page = new PageImpl<>(List.of(new BillingInfoEntity()));
        final BillingInfoPage pageDto = new BillingInfoPage();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(service.list(tenant, query, pageable)).thenReturn(page);
        when(mapper.mapToDto(page)).thenReturn(pageDto);

        final ResponseEntity<BillingInfoPage> resp = controller.listBillingInfos(query, pageable);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(pageDto);
        verify(service).list(tenant, query, pageable);
        verify(mapper).mapToDto(page);
    }

    @Test
    void shouldReturnCreatedWhenCreateWithConfirmTrue() {
        final String tenant = "t1";
        final BillingInfoCreateRequest req = new BillingInfoCreateRequest().confirm(true);
        final BillingInfoEntity entity = new BillingInfoEntity();
        final BillingInfo dto = new BillingInfo();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(mapper.toEntity(req)).thenReturn(entity);
        when(service.create(tenant, entity, true)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<BillingInfo> resp = controller.createBillingInfo(req);

        assertThat(resp.getStatusCode().value()).isEqualTo(201);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(service).create(tenant, entity, true);
    }

    @Test
    void shouldPassThroughFalseWhenCreateWithConfirmNull() {
        final String tenant = "t1";
        final BillingInfoCreateRequest req = new BillingInfoCreateRequest().confirm(null);
        final BillingInfoEntity entity = new BillingInfoEntity();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(mapper.toEntity(req)).thenReturn(entity);
        when(service.create(tenant, entity, false)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new BillingInfo());

        final ResponseEntity<BillingInfo> resp = controller.createBillingInfo(req);

        assertThat(resp.getStatusCode().value()).isEqualTo(201);
        verify(service).create(tenant, entity, false);
    }

    @Test
    void shouldApplyMapperAndReturnOkWhenUpdate() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        final BillingInfoUpdateRequest req = new BillingInfoUpdateRequest();
        final BillingInfoEntity entity = new BillingInfoEntity();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(service.update(eq(tenant), eq(id), any())).thenAnswer(inv -> {
            final java.util.function.Consumer<BillingInfoEntity> updater = inv.getArgument(2);
            updater.accept(entity);
            return entity;
        });
        when(mapper.toDto(entity)).thenReturn(new BillingInfo());

        final ResponseEntity<BillingInfo> resp = controller.updateBillingInfo(id, req);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        verify(mapper).updateEntity(req, entity);
        verify(service).update(eq(tenant), eq(id), any());
    }

    @Test
    void shouldReturnNoContentWhenDelete() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(tenantProvider.requireTenantId()).thenReturn(tenant);

        final ResponseEntity<Void> resp = controller.deleteBillingInfo(id);

        assertThat(resp.getStatusCode().value()).isEqualTo(204);
        verify(service).delete(tenant, id);
    }
}
