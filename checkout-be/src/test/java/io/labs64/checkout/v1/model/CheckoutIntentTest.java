package io.labs64.checkout.v1.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.v1.model.CheckoutIntent;
import io.labs64.checkout.v1.model.CheckoutIntentStatus;
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
        return new CheckoutIntent().amount(new BigDecimal("149.99")).currency("USD").paymentMethod("STRIPE")
                .billingInfoId(UUID.randomUUID()).extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldPassWhenAmountIsNull() {
        final CheckoutIntent ci = validCheckoutIntent().amount(null);
        assertThat(validator.validateProperty(ci, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsZero() {
        final CheckoutIntent ci = validCheckoutIntent().amount(BigDecimal.ZERO);
        assertThat(validator.validateProperty(ci, "amount")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = { "-0.01", "-1", "-100" })
    void shouldFailWhenAmountIsNegative(final String val) {
        final CheckoutIntent ci = validCheckoutIntent().amount(new BigDecimal(val));
        assertThat(validator.validateProperty(ci, "amount")).isNotEmpty();
    }
}
