package io.labs64.checkout.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.labs64.checkout.api.CheckoutIntentApi;
import io.labs64.checkout.entity.BillingInfoEntity;
import io.labs64.checkout.entity.CheckoutIntentEntity;
import io.labs64.checkout.mapper.CheckoutIntentMapper;
import io.labs64.checkout.model.CheckoutIntent;
import io.labs64.checkout.model.CheckoutIntentCreateRequest;
import io.labs64.checkout.model.CheckoutIntentPage;
import io.labs64.checkout.model.CheckoutIntentUpdateRequest;
import io.labs64.checkout.service.BillingInfoService;
import io.labs64.checkout.service.CheckoutIntentService;
import io.labs64.checkout.web.tenant.RequestTenantProvider;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CheckoutIntentController implements CheckoutIntentApi {

    private final RequestTenantProvider tenantProvider;
    private final CheckoutIntentService checkoutIntentService;
    private final BillingInfoService billingInfoService;
    private final CheckoutIntentMapper mapper;

    @Override
    public ResponseEntity<CheckoutIntent> getCheckoutIntent(final UUID id) {
        final String tenantId = tenantProvider.requireTenantId();

        final CheckoutIntentEntity entity = checkoutIntentService.get(tenantId, id);
        final CheckoutIntent response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CheckoutIntentPage> listCheckoutIntents(final String query, final Pageable pageable) {
        final String tenantId = tenantProvider.requireTenantId();

        final Page<CheckoutIntentEntity> list = checkoutIntentService.list(tenantId, query, pageable);
        final CheckoutIntentPage response = mapper.mapToDto(list);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CheckoutIntent> createCheckoutIntent(@Valid final CheckoutIntentCreateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final UUID billingInfoId = request.getBillingInfoId();

        final CheckoutIntentEntity newEntity = mapper.toEntity(request);

        if (billingInfoId != null) {
            final BillingInfoEntity billingInfo = billingInfoService.get(tenantId, billingInfoId);
            newEntity.setBillingInfo(billingInfo);
        }

        final CheckoutIntentEntity entity = checkoutIntentService.create(tenantId, newEntity);
        final CheckoutIntent response = mapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<CheckoutIntent> updateCheckoutIntent(final UUID id,
            @Valid final CheckoutIntentUpdateRequest request) {
        final String tenantId = tenantProvider.requireTenantId();
        final UUID billingInfoId = request.getBillingInfoId();

        final CheckoutIntentEntity entity = checkoutIntentService.update(tenantId, id, (ci) -> {
            mapper.updateEntity(request, ci);

            if (billingInfoId != null) {
                ci.setBillingInfo(billingInfoService.get(tenantId, billingInfoId));
            }
        });

        final CheckoutIntent response = mapper.toDto(entity);

        return ResponseEntity.ok(response);
    }
}
