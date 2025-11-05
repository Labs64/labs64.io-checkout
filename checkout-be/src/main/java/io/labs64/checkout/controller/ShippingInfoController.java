package io.labs64.checkout.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.api.ShippingInfoApi;
import io.labs64.checkout.entity.ShippingInfoEntity;
import io.labs64.checkout.exception.ConsentRequiredException;
import io.labs64.checkout.mapper.ShippingInfoMapper;
import io.labs64.checkout.messages.ShippingInfoMessages;
import io.labs64.checkout.model.ShippingInfo;
import io.labs64.checkout.model.ShippingInfoCreateRequest;
import io.labs64.checkout.model.ShippingInfoPage;
import io.labs64.checkout.model.ShippingInfoUpdateRequest;
import io.labs64.checkout.service.ShippingInfoService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
public class ShippingInfoController implements ShippingInfoApi {

    private final RequestTenantProvider tenantProvider;
    private final ShippingInfoService service;
    private final ShippingInfoMapper mapper;
    private final ShippingInfoMessages msg;

    @Override
    public ResponseEntity<ShippingInfo> getShippingInfo(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();

        final ShippingInfoEntity entity = service.get(tenantId, id);
        final ShippingInfo response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ShippingInfoPage> listShippingInfos(final String query, final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();

        final Page<ShippingInfoEntity> list = service.list(tenantId, query, pageable);
        final ShippingInfoPage response = mapper.mapToDto(list);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ShippingInfo> createShippingInfo(@Valid final ShippingInfoCreateRequest request) {
        // TODO(RVA): remove to service
        if (!Boolean.TRUE.equals(request.getConfirm())) {
            throw new ConsentRequiredException(msg.consentRequired());
        }

        final String tenantId = tenantProvider.requireTenantId();
        final ShippingInfoEntity entity = service.create(tenantId, mapper.toEntity(request));
        final ShippingInfo response = mapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ShippingInfo> updateShippingInfo(final UUID id,
            @Valid final ShippingInfoUpdateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();

        final ShippingInfoEntity entity = service.update(tenantId, id, (bi) -> mapper.updateEntity(request, bi));
        final ShippingInfo response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteShippingInfo(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();
        service.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
