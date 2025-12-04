package io.labs64.checkout.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import jakarta.validation.constraints.NotBlank;

@Repository
public interface CheckoutTransactionRepository extends JpaRepository<CheckoutTransactionEntity, UUID> {
    Optional<CheckoutTransactionEntity> findByIdAndTenantId(UUID id, @NotBlank String tenantId);

    Page<CheckoutTransactionEntity> findByTenantId(String tenantId, Pageable pageable);
}
