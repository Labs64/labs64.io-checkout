package io.labs64.checkout.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.entity.PurchaseOrderItemEntity;
import io.labs64.checkout.model.PurchaseOrder;
import io.labs64.checkout.model.PurchaseOrderCreateRequest;
import io.labs64.checkout.model.PurchaseOrderPage;
import io.labs64.checkout.model.PurchaseOrderUpdateRequest;
import io.labs64.checkout.util.AmountCalculator;

@Mapper(config = MapperConfigBase.class, uses = { PurchaseOrderItemMapper.class, CurrencyMapper.class,
        CustomerMapper.class })
public interface PurchaseOrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "currency", source = "currency", qualifiedByName = "upperCurrency")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PurchaseOrderEntity toEntity(PurchaseOrderCreateRequest source);

    @Mapping(target = "netAmount", ignore = true)
    @Mapping(target = "grossAmount", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    PurchaseOrder toDto(PurchaseOrderEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "startsAt", ignore = true)
    @Mapping(target = "endsAt", ignore = true)
    void updateEntity(PurchaseOrderUpdateRequest source, @MappingTarget PurchaseOrderEntity target);

    @Mapping(target = "sort", ignore = true)
    default PurchaseOrderPage toPage(final Page<PurchaseOrderEntity> source) {
        final PurchaseOrderPage page = new PurchaseOrderPage();

        final List<PurchaseOrder> items = source.getContent().stream().map(this::toDto).toList();

        page.setItems(items);
        page.setPage(source.getNumber());
        page.setPageSize(source.getSize());
        page.setTotalItems(source.getTotalElements());
        page.setTotalPages(source.getTotalPages());
        page.setHasPrev(source.hasPrevious());
        page.setHasNext(source.hasNext());

        return page;
    }

    @AfterMapping
    default void afterToEntity(final PurchaseOrderCreateRequest source,
            @MappingTarget final PurchaseOrderEntity target) {
        final List<PurchaseOrderItemEntity> items = target.getItems();

        if (items != null) {
            for (final PurchaseOrderItemEntity item : items) {
                item.setPurchaseOrder(target);
            }
        }
    }

    @AfterMapping
    default void afterToDto(final PurchaseOrderEntity source, @MappingTarget final PurchaseOrder target) {
        final AmountCalculator.Amounts totals = AmountCalculator.calculate(source);
        target.setNetAmount(totals.netAmount());
        target.setTaxAmount(totals.taxAmount());
        target.setGrossAmount(totals.grossAmount());
    }
}
