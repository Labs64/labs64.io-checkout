package io.labs64.checkout.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.entity.CheckoutIntentEntity;
import io.labs64.checkout.model.CheckoutIntent;
import io.labs64.checkout.model.CheckoutIntentCreateRequest;
import io.labs64.checkout.model.CheckoutIntentPage;
import io.labs64.checkout.model.CheckoutIntentUpdateRequest;

@Mapper(componentModel = "spring", uses = { CurrencyMapper.class, BillingInfoMapper.class })
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
}