package io.labs64.checkout.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyMapperTest {
    private final CurrencyMapper mapper = new CurrencyMapper() {
    };

    @Test
    void upperCurrencyNullReturnsNull() {
        assertNull(mapper.upperCurrency(null));
    }

    @Test
    void upperCurrencyEmptyReturnsNull() {
        assertNull(mapper.upperCurrency(""));
    }

    @Test
    void upperCurrencyTrimsAndUppercases() {
        final String result = mapper.upperCurrency("  usd  ");

        assertEquals("USD", result);
    }

    @Test
    void upperCurrencyWhitespaceOnlyBecomesEmptyString() {
        final String result = mapper.upperCurrency("   ");

        assertEquals("", result);
    }
}
