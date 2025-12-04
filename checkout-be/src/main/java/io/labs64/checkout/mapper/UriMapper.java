package io.labs64.checkout.mapper;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UriMapper {
    default String uriToString(final URI uri) {
        return uri == null ? null : uri.toString();
    }

    default URI stringToUri(final String uri) {
        return uri == null ? null : URI.create(uri);
    }

    default Set<String> uriSetToStringSet(final Set<URI> source) {
        if (source == null) {
            return null;
        }

        return source.stream().map(this::uriToString).collect(Collectors.toCollection(HashSet::new));
    }

    default Set<URI> stringSetToUriSet(final Set<String> source) {
        if (source == null) {
            return null;
        }

        return source.stream().map(this::stringToUri).collect(Collectors.toCollection(HashSet::new));
    }
}
