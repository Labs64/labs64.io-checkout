package io.labs64.checkout.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.entity.ShippingInfoEntity;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfoEntity, UUID> {
    Optional<ShippingInfoEntity> findByIdAndTenantId(UUID id, String tenantId);

    Page<ShippingInfoEntity> findByTenantId(String tenantId, Pageable pageable);

    int deleteByIdAndTenantId(UUID id, String tenantId);
}
