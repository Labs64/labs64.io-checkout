package io.labs64.checkout.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.labs64.checkout.entity.CheckoutTransactionEntity;

public interface CheckoutTransactionService {
    Optional<CheckoutTransactionEntity> find(final UUID id);

    CheckoutTransactionEntity get(final UUID id);

    Optional<CheckoutTransactionEntity> find(final String tenantId, final UUID id);

    CheckoutTransactionEntity get(final String tenantId, final UUID id);

    Page<CheckoutTransactionEntity> list(final String tenantId, final String query, final Pageable pageable);

    CheckoutTransactionEntity create(String tenantId, CheckoutTransactionEntity entity);
}
