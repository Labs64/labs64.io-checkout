package io.labs64.checkout.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface NullablesMapper {
    @Named("wrap")
    default <T> JsonNullable<T> wrap(final T source) {
        return source == null ? JsonNullable.undefined() : JsonNullable.of(source);
    }

    @Named("unwrap")
    default <T> T unwrap(final JsonNullable<T> source) {
        if (source == null) {
            return null;
        }

        return source.isPresent() ? source.get() : null;
    }
}
