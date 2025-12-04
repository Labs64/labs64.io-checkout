package io.labs64.checkout.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.messages.CheckoutTransactionMessages;
import io.labs64.checkout.repository.CheckoutTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckoutTransactionServiceImpl implements CheckoutTransactionService {
    private final CheckoutTransactionRepository repository;
    private final CheckoutTransactionMessages msg;

    @Override
    @Transactional(readOnly = true)
    public Optional<CheckoutTransactionEntity> find(final UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutTransactionEntity get(final UUID id) {
        return find(id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CheckoutTransactionEntity> find(final String tenantId, final UUID id) {
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutTransactionEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CheckoutTransactionEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.isNotBlank(query)) {
            // TODO(RVA): implement query reader(NLQL)
            return Page.empty();
        }
        return repository.findByTenantId(tenantId, pageable);
    }

    @Override
    @Transactional
    public CheckoutTransactionEntity create(final String tenantId, final CheckoutTransactionEntity entity) {
        entity.setTenantId(tenantId);
        final CheckoutTransactionEntity saved = repository.save(entity);

        log.debug("Create checkout transaction: {}", saved);
        return saved;
    }
}
