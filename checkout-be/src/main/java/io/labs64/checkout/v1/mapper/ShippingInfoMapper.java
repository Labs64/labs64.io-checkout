package io.labs64.checkout.v1.mapper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.v1.entity.BillingInfoEntity;
import io.labs64.checkout.v1.entity.ShippingInfoEntity;
import io.labs64.checkout.v1.model.ShippingInfo;
import io.labs64.checkout.v1.model.ShippingInfoCreateRequest;
import io.labs64.checkout.v1.model.ShippingInfoPage;
import io.labs64.checkout.v1.model.ShippingInfoUpdateRequest;

@Mapper(componentModel = "spring")
public interface ShippingInfoMapper {

    ShippingInfoEntity toEntity(ShippingInfoCreateRequest source);

    ShippingInfo toDto(ShippingInfoEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ShippingInfoUpdateRequest source, @MappingTarget ShippingInfoEntity target);

    @Mapping(target = "sort", ignore = true)
    default ShippingInfoPage mapToDto(final Page<ShippingInfoEntity> source) {
        final ShippingInfoPage page = new ShippingInfoPage();

        final List<ShippingInfo> items = source.getContent().stream().map(this::toDto).toList();

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
