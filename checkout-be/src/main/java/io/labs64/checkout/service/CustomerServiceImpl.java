package io.labs64.checkout.service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.exception.NotFoundException;
import io.labs64.checkout.exception.ValidationException;
import io.labs64.checkout.messages.CustomerMessages;
import io.labs64.checkout.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMessages msg;

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerEntity> find(final String tenantId, final UUID id) {
        return repository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerEntity get(final String tenantId, final UUID id) {
        return find(tenantId, id).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerEntity> list(final String tenantId, final String query, final Pageable pageable) {
        if (StringUtils.isNotBlank(query)) {
            // TODO(RVA): implement query reader
            return Page.empty();
        }

        return repository.findByTenantId(tenantId, pageable);
    }

    @Override
    @Transactional
    public CustomerEntity create(final String tenantId, final CustomerEntity entity) {
        entity.setTenantId(tenantId);

        // check email
        final String email = entity.getEmail();

        if (StringUtils.isNotBlank(email) && repository.existsByTenantIdAndEmail(tenantId, email)) {
            throw new ValidationException(msg.emailDuplicate(email));
        }

        final CustomerEntity saved = repository.save(entity);

        log.debug("Create customer: {}", saved);
        return saved;
    }

    @Override
    @Transactional
    public CustomerEntity update(final String tenantId, final UUID id, final Consumer<CustomerEntity> updater) {
        return repository.findByIdAndTenantId(id, tenantId).map((entity) -> {
            updater.accept(entity);

            log.debug("Update customer: {}", entity);

            return entity;
        }).orElseThrow(() -> new NotFoundException(msg.notFound(id)));
    }

    @Override
    @Transactional
    public boolean delete(final String tenantId, final UUID id) {
        final int affected = repository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Delete customer id={} tenant={} affected={}", id, tenantId, affected);
        return affected > 0;
    }
}
