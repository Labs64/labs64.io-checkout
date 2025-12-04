package io.labs64.checkout.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.labs64.checkout.entity.PurchaseOrderItemEntity;
import io.labs64.checkout.model.PurchaseOrderItem;

@Mapper(config = MapperConfigBase.class, uses = { UriMapper.class })
public interface PurchaseOrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PurchaseOrderItemEntity toEntity(PurchaseOrderItem source);

    List<PurchaseOrderItemEntity> toEntity(List<PurchaseOrderItem> source);

    PurchaseOrderItem toDto(PurchaseOrderItemEntity source);

    List<PurchaseOrderItem> toDto(List<PurchaseOrderItemEntity> source);
}
