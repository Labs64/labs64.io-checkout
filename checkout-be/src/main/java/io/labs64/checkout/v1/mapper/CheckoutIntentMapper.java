package io.labs64.checkout.v1.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.v1.entity.CheckoutIntentEntity;
import io.labs64.checkout.v1.model.CheckoutIntent;
import io.labs64.checkout.v1.model.CheckoutIntentCreateRequest;
import io.labs64.checkout.v1.model.CheckoutIntentPage;
import io.labs64.checkout.v1.model.CheckoutIntentUpdateRequest;

@Mapper(componentModel = "spring", uses = { BillingInfoMapper.class })
public interface CheckoutIntentMapper {

    @Mapping(target = "currency", source = "currency", qualifiedByName = "upperCurrency")
    CheckoutIntentEntity toEntity(CheckoutIntentCreateRequest source);

    CheckoutIntent toDto(CheckoutIntentEntity entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CheckoutIntentUpdateRequest source, @MappingTarget CheckoutIntentEntity target);

    @Mapping(target = "sort", ignore = true)
    default CheckoutIntentPage mapToDto(final Page<CheckoutIntentEntity> source) {
        final CheckoutIntentPage page = new CheckoutIntentPage();

        final List<CheckoutIntent> items = source.getContent().stream().map(this::toDto).toList();

        page.setItems(items);
        page.setPage(source.getNumber());
        page.setPageSize(source.getSize());
        page.setTotalItems(source.getTotalElements());
        page.setTotalPages(source.getTotalPages());
        page.setHasPrev(source.hasPrevious());
        page.setHasNext(source.hasNext());

        return page;
    }

    @Named("upperCurrency")
    default String upperCurrency(final String s) {
        return (s == null || s.isEmpty()) ? null : s.trim().toUpperCase();
    }
}