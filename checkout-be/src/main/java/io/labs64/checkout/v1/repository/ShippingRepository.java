package io.labs64.checkout.v1.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.v1.entity.ShippingEntity;

@Repository
public interface ShippingRepository extends JpaRepository<ShippingEntity, UUID> {
    Optional<ShippingEntity> findByIdAndTenantId(UUID id, String tenantId);

    Page<ShippingEntity> findByTenantId(String tenantId, Pageable pageable);

    boolean existsByTenantIdAndShippingInfoId(String tenantId, UUID shippingInfoId);

    int deleteByIdAndTenantId(UUID id, String tenantId);
}
