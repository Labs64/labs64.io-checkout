package io.labs64.checkout.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import io.labs64.checkout.entity.CheckoutTransactionEntity;
import io.labs64.checkout.model.CheckoutTransaction;
import io.labs64.checkout.model.CheckoutTransactionPage;

@Mapper(config = MapperConfigBase.class, uses = { PurchaseOrderMapper.class })
public interface CheckoutTransactionMapper {
    CheckoutTransaction toDto(CheckoutTransactionEntity source);

    @Mapping(target = "sort", ignore = true)
    default CheckoutTransactionPage toPage(final Page<CheckoutTransactionEntity> source) {
        final CheckoutTransactionPage page = new CheckoutTransactionPage();

        final List<CheckoutTransaction> items = source.getContent().stream().map(this::toDto).toList();

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
