package io.labs64.checkout.model;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.CheckoutIntent;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CheckoutIntentTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static CheckoutIntent validCheckoutIntent() {
        return new CheckoutIntent().amount(14999L).currency("USD").paymentMethod("STRIPE")
                .billingInfoId(UUID.randomUUID()).extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldPassWhenAmountIsNull() {
        final CheckoutIntent ci = validCheckoutIntent().amount(null);
        assertThat(validator.validateProperty(ci, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsZero() {
        final CheckoutIntent ci = validCheckoutIntent().amount(0L);
        assertThat(validator.validateProperty(ci, "amount")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(longs = { -1L, -100L })
    void shouldFailWhenAmountIsNegative(final Long val) {
        final CheckoutIntent ci = validCheckoutIntent().amount(val);
        assertThat(validator.validateProperty(ci, "amount")).isNotEmpty();
    }
}
