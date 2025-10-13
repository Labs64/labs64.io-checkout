package io.labs64.checkout.v1.controller;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.labs64.checkout.exception.ConsentRequiredException;
import io.labs64.checkout.messages.ShippingInfoMessages;
import io.labs64.checkout.v1.entity.ShippingInfoEntity;
import io.labs64.checkout.v1.mapper.ShippingInfoMapper;
import io.labs64.checkout.v1.model.ShippingInfo;
import io.labs64.checkout.v1.model.ShippingInfoCreateRequest;
import io.labs64.checkout.v1.model.ShippingInfoPage;
import io.labs64.checkout.v1.model.ShippingInfoUpdateRequest;
import io.labs64.checkout.v1.service.ShippingInfoService;
import io.labs64.checkout.v1.web.tenant.RequestTenantProvider;

class ShippingInfoControllerTest {

    private final RequestTenantProvider tenantProvider = mock(RequestTenantProvider.class);
    private final ShippingInfoService service = mock(ShippingInfoService.class);
    private final ShippingInfoMapper mapper = mock(ShippingInfoMapper.class);
    private final ShippingInfoMessages msg = mock(ShippingInfoMessages.class);

    private final ShippingInfoController controller = new ShippingInfoController(tenantProvider, service, mapper, msg);

    @Test
    void shouldReturnOkAndBodyWhenGet() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();

        final ShippingInfoEntity entity = new ShippingInfoEntity();
        final ShippingInfo dto = new ShippingInfo();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(service.get(tenant, id)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<ShippingInfo> resp = controller.getShippingInfo(id);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(service).get(tenant, id);
        verify(mapper).toDto(entity);
    }

    @Test
    void shouldReturnOkAndPageWhenList() {
        final String tenant = "t1";
        final String query = "city:Kyiv";
        final Pageable pageable = Pageable.unpaged();

        final Page<ShippingInfoEntity> page = new PageImpl<>(List.of(new ShippingInfoEntity()));
        final ShippingInfoPage pageDto = new ShippingInfoPage();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(service.list(tenant, query, pageable)).thenReturn(page);
        when(mapper.mapToDto(page)).thenReturn(pageDto);

        final ResponseEntity<ShippingInfoPage> resp = controller.listShippingInfos(query, pageable);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(pageDto);
        verify(service).list(tenant, query, pageable);
        verify(mapper).mapToDto(page);
    }

    @Test
    void shouldReturnCreatedWhenCreateWithConfirmTrue() {
        final String tenant = "t1";
        final ShippingInfoCreateRequest req = new ShippingInfoCreateRequest().confirm(true);
        final ShippingInfoEntity toCreate = new ShippingInfoEntity();
        final ShippingInfoEntity saved = new ShippingInfoEntity();
        final ShippingInfo dto = new ShippingInfo();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(mapper.toEntity(req)).thenReturn(toCreate);
        when(service.create(tenant, toCreate)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        final ResponseEntity<ShippingInfo> resp = controller.createShippingInfo(req);

        assertThat(resp.getStatusCode().value()).isEqualTo(201);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(service).create(tenant, toCreate);
        verify(mapper).toDto(saved);
    }

    @Test
    void shouldThrowConsentRequiredWhenCreateWithoutConfirmTrue() {
        final ShippingInfoCreateRequest req = new ShippingInfoCreateRequest().confirm(null);
        when(msg.consentRequired()).thenReturn("consent");

        assertThatThrownBy(() -> controller.createShippingInfo(req)).isInstanceOf(ConsentRequiredException.class)
                .hasMessage("consent");
        verifyNoInteractions(service);
    }

    @Test
    void shouldApplyUpdaterAndReturnOkWhenUpdate() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();

        final ShippingInfoUpdateRequest req = new ShippingInfoUpdateRequest();
        final ShippingInfoEntity entity = new ShippingInfoEntity();
        final ShippingInfo dto = new ShippingInfo();

        when(tenantProvider.requireTenantId()).thenReturn(tenant);
        when(service.update(eq(tenant), eq(id), any())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            final java.util.function.Consumer<ShippingInfoEntity> updater = inv.getArgument(2);
            updater.accept(entity);
            return entity;
        });
        when(mapper.toDto(entity)).thenReturn(dto);

        final ResponseEntity<ShippingInfo> resp = controller.updateShippingInfo(id, req);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isSameAs(dto);
        verify(mapper).updateEntity(req, entity);
        verify(service).update(eq(tenant), eq(id), any());
    }

    @Test
    void shouldReturnNoContentWhenDelete() {
        final String tenant = "t1";
        final UUID id = UUID.randomUUID();
        when(tenantProvider.requireTenantId()).thenReturn(tenant);

        final ResponseEntity<Void> resp = controller.deleteShippingInfo(id);

        assertThat(resp.getStatusCode().value()).isEqualTo(204);
        verify(service).delete(tenant, id);
    }
}
