package io.labs64.checkout.v1.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.v1.entity.BillingInfoEntity;

@Repository
public interface BillingInfoRepository extends JpaRepository<BillingInfoEntity, UUID> {
    Optional<BillingInfoEntity> findByIdAndTenantId(UUID id, String tenantId);

    Page<BillingInfoEntity> findByTenantId(String tenantId, Pageable pageable);

    int deleteByIdAndTenantId(UUID id, String tenantId);
}
