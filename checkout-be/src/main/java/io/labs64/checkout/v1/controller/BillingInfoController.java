package io.labs64.checkout.v1.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.v1.api.BillingInfoApi;
import io.labs64.checkout.v1.entity.BillingInfoEntity;
import io.labs64.checkout.v1.mapper.BillingInfoMapper;
import io.labs64.checkout.v1.model.BillingInfo;
import io.labs64.checkout.v1.model.BillingInfoCreateRequest;
import io.labs64.checkout.v1.model.BillingInfoPage;
import io.labs64.checkout.v1.model.BillingInfoUpdateRequest;
import io.labs64.checkout.v1.service.BillingInfoService;
import io.labs64.checkout.v1.web.tenant.RequestTenantProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class BillingInfoController implements BillingInfoApi {

    private final RequestTenantProvider tenantProvider;
    private final BillingInfoService service;
    private final BillingInfoMapper mapper;

    @Override
    public ResponseEntity<BillingInfo> getBillingInfo(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();

        final BillingInfoEntity entity = service.get(tenantId, id);
        final BillingInfo response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BillingInfoPage> listBillingInfos(final String query, final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();

        final Page<BillingInfoEntity> list = service.list(tenantId, query, pageable);
        final BillingInfoPage response = mapper.mapToDto(list);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BillingInfo> createBillingInfo(@Valid final BillingInfoCreateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final BillingInfoEntity entity = service.create(tenantId, mapper.toEntity(request),
                Boolean.TRUE.equals(request.getConfirm()));
        final BillingInfo response = mapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<BillingInfo> updateBillingInfo(final UUID id, @Valid final BillingInfoUpdateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();

        final BillingInfoEntity entity = service.update(tenantId, id, (bi) -> mapper.updateEntity(request, bi));
        final BillingInfo response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteBillingInfo(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();
        service.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
