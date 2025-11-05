package io.labs64.checkout.mapper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.entity.BillingInfoEntity;
import io.labs64.checkout.model.BillingInfo;
import io.labs64.checkout.model.BillingInfoCreateRequest;
import io.labs64.checkout.model.BillingInfoPage;
import io.labs64.checkout.model.BillingInfoUpdateRequest;

@Mapper(componentModel = "spring")
public interface BillingInfoMapper {

    BillingInfoEntity toEntity(BillingInfoCreateRequest source);

    BillingInfo toDto(BillingInfoEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(BillingInfoUpdateRequest source, @MappingTarget BillingInfoEntity target);

    @Mapping(target = "sort", ignore = true)
    default BillingInfoPage mapToDto(final Page<BillingInfoEntity> source) {
        final BillingInfoPage page = new BillingInfoPage();

        final List<BillingInfo> items = source.getContent().stream().map(this::toDto).toList();

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
