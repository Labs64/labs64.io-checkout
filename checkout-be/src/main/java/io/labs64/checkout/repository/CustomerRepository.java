package io.labs64.checkout.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.labs64.checkout.entity.CustomerEntity;
import jakarta.validation.constraints.NotBlank;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByIdAndTenantId(UUID id, @NotBlank String tenantId);

    int deleteByIdAndTenantId(UUID id, @NotBlank String tenantId);

    Page<CustomerEntity> findByTenantId(String tenantId, Pageable pageable);
}
