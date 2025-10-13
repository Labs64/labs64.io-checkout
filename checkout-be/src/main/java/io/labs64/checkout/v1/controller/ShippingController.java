package io.labs64.checkout.v1.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.messages.ShippingMessages;
import io.labs64.checkout.v1.api.ShippingApi;
import io.labs64.checkout.v1.entity.ShippingEntity;
import io.labs64.checkout.v1.mapper.ShippingMapper;
import io.labs64.checkout.v1.model.Shipping;
import io.labs64.checkout.v1.model.ShippingCreateRequest;
import io.labs64.checkout.v1.model.ShippingPage;
import io.labs64.checkout.v1.model.ShippingUpdateRequest;
import io.labs64.checkout.v1.service.ShippingService;
import io.labs64.checkout.v1.web.tenant.RequestTenantProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class ShippingController implements ShippingApi {

    private final RequestTenantProvider tenantProvider;
    private final ShippingService service;
    private final ShippingMapper mapper;
    private final ShippingMessages msg;

    @Override
    public ResponseEntity<Shipping> getShipping(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();

        final ShippingEntity entity = service.get(tenantId, id);
        final Shipping response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ShippingPage> listShipping(final String query, final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();

        final Page<ShippingEntity> list = service.list(tenantId, query, pageable);
        final ShippingPage response = mapper.mapToDto(list);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Shipping> createShipping(@Valid final ShippingCreateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();

        final ShippingEntity entity = service.create(tenantId, request.getShippingInfoId(),
                request.getCheckoutIntentId(), mapper.toEntity(request));
        final Shipping response = mapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Shipping> updateShipping(final UUID id, @Valid final ShippingUpdateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();

        final ShippingEntity entity = service.update(tenantId, id, (bi) -> mapper.updateEntity(request, bi));
        final Shipping response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }
}
