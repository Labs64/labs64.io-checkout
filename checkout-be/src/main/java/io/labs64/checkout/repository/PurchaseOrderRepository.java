package io.labs64.checkout.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.entity.PurchaseOrderEntity;
import jakarta.validation.constraints.NotBlank;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, UUID> {
    Optional<PurchaseOrderEntity> findByIdAndTenantId(UUID id, @NotBlank String tenantId);

    int deleteByIdAndTenantId(UUID id, @NotBlank String tenantId);

    Page<PurchaseOrderEntity> findByTenantId(String tenantId, Pageable pageable);
}
