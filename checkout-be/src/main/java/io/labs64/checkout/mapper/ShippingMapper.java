package io.labs64.checkout.mapper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.entity.ShippingEntity;
import io.labs64.checkout.model.Shipping;
import io.labs64.checkout.model.ShippingCreateRequest;
import io.labs64.checkout.model.ShippingPage;
import io.labs64.checkout.model.ShippingUpdateRequest;

@Mapper(componentModel = "spring")
public interface ShippingMapper {

    ShippingEntity toEntity(ShippingCreateRequest source);

    Shipping toDto(ShippingEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ShippingUpdateRequest source, @MappingTarget ShippingEntity target);

    @Mapping(target = "sort", ignore = true)
    default ShippingPage mapToDto(final Page<ShippingEntity> source) {
        final ShippingPage page = new ShippingPage();

        final List<Shipping> items = source.getContent().stream().map(this::toDto).toList();

        page.setItems(items);
        page.setPage(source.getNumber());
        page.setPageSize(source.getSize());
        page.setTotalItems(source.getTotalElements());
        page.setTotalPages(source.getTotalPages());
        page.setHasPrev(source.hasPrevious());
        page.setHasNext(source.hasNext());

        return page;
    }

    default String map(final String value) {
        return StringUtils.isBlank(value) ? null : value;
    }
}
