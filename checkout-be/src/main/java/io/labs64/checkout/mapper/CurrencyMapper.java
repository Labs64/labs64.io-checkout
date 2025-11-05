package io.labs64.checkout.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CurrencyMapper {
    @Named("upperCurrency")
    default String upperCurrency(final String s) {
        return (s == null || s.isEmpty()) ? null : s.trim().toUpperCase();
    }
}
