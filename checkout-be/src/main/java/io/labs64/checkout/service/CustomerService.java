package io.labs64.checkout.service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.labs64.checkout.entity.CustomerEntity;

public interface CustomerService {
    Optional<CustomerEntity> find(final String tenantId, final UUID id);

    CustomerEntity get(final String tenantId, final UUID id);

    Page<CustomerEntity> list(final String tenantId, final String query, final Pageable pageable);

    CustomerEntity create(String tenantId, CustomerEntity entity);

    CustomerEntity update(final String tenantId, final UUID id, final Consumer<CustomerEntity> updater);

    boolean delete(final String tenantId, final UUID id);
}
