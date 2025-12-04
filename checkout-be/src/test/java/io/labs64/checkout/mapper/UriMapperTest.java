package io.labs64.checkout.mapper;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UriMapperTest {

    private final UriMapper mapper = new UriMapper() {
    };

    @Test
    void uriToStringNullReturnsNull() {
        assertNull(mapper.uriToString(null));
    }

    @Test
    void uriToStringConvertsUriToString() {
        final URI uri = URI.create("https://example.com/path");

        final String result = mapper.uriToString(uri);

        assertEquals("https://example.com/path", result);
    }

    @Test
    void stringToUriNullReturnsNull() {
        assertNull(mapper.stringToUri(null));
    }

    @Test
    void stringToUriConvertsStringToUri() {
        final String value = "https://example.com/path";

        final URI result = mapper.stringToUri(value);

        assertNotNull(result);
        assertEquals(value, result.toString());
    }

    @Test
    void uriSetToStringSetNullReturnsNull() {
        assertNull(mapper.uriSetToStringSet(null));
    }

    @Test
    void uriSetToStringSetConvertsEachElement() {
        final Set<URI> source = new HashSet<>();
        source.add(URI.create("https://a.example.com"));
        source.add(URI.create("https://b.example.com"));

        final Set<String> result = mapper.uriSetToStringSet(source);

        assertNotNull(result);
        assertEquals(source.size(), result.size());
        assertTrue(result.contains("https://a.example.com"));
        assertTrue(result.contains("https://b.example.com"));
    }

    @Test
    void stringSetToUriSetNullReturnsNull() {
        assertNull(mapper.stringSetToUriSet(null));
    }

    @Test
    void stringSetToUriSetConvertsEachElement() {
        final Set<String> source = new HashSet<>();
        source.add("https://a.example.com");
        source.add("https://b.example.com");

        final Set<URI> result = mapper.stringSetToUriSet(source);

        assertNotNull(result);
        assertEquals(source.size(), result.size());
        assertTrue(result.contains(URI.create("https://a.example.com")));
        assertTrue(result.contains(URI.create("https://b.example.com")));
    }
}
