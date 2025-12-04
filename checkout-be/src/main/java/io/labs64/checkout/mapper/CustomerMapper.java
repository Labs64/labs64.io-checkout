package io.labs64.checkout.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.entity.CustomerEntity;
import io.labs64.checkout.model.Customer;
import io.labs64.checkout.model.CustomerCreateRequest;
import io.labs64.checkout.model.CustomerPage;
import io.labs64.checkout.model.CustomerUpdateRequest;

@Mapper(config = MapperConfigBase.class)
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CustomerEntity toEntity(CustomerCreateRequest source);

    Customer toDto(CustomerEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CustomerUpdateRequest source, @MappingTarget CustomerEntity target);

    @Mapping(target = "sort", ignore = true)
    default CustomerPage toPage(final Page<CustomerEntity> source) {
        final CustomerPage page = new CustomerPage();

        final List<Customer> items = source.getContent().stream().map(this::toDto).toList();

        page.setItems(items);
        page.setPage(source.getNumber());
        page.setPageSize(source.getSize());
        page.setTotalItems(source.getTotalElements());
        page.setTotalPages(source.getTotalPages());
        page.setHasPrev(source.hasPrevious());
        page.setHasNext(source.hasNext());

        return page;
    }
}
