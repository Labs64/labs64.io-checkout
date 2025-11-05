package io.labs64.checkout.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.entity.CheckoutIntentEntity;
import io.labs64.checkout.model.CheckoutIntentStatus;

@Repository
public interface CheckoutIntentRepository extends JpaRepository<CheckoutIntentEntity, UUID> {
    Optional<CheckoutIntentEntity> findByIdAndTenantId(UUID id, String tenantId);

    Page<CheckoutIntentEntity> findByTenantId(String tenantId, Pageable pageable);

    boolean existsByTenantIdAndBillingInfoId(String tenantId, UUID billingInfoId);

    boolean existsByTenantIdAndBillingInfoIdAndStatusIn(String tenantId, UUID billingInfoId,
            Set<CheckoutIntentStatus> statuses);

    boolean existsByTenantIdAndShippingId(String tenantId, UUID shippingId);
}
