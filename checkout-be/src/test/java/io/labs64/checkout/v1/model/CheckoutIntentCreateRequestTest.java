package io.labs64.checkout.v1.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;

import io.labs64.checkout.v1.model.CheckoutIntentCreateRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutIntentCreateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static io.labs64.checkout.v1.model.CheckoutIntentCreateRequest valid() {
        return new CheckoutIntentCreateRequest(new BigDecimal("149.99"), "USD", "STRIPE");
    }

    @Test
    void shouldPassWhenAmountIsZero() {
        final CheckoutIntentCreateRequest r = valid().amount(BigDecimal.ZERO);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsPositive() {
        final CheckoutIntentCreateRequest r = valid().amount(new BigDecimal("0.01"));
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldFailWhenAmountIsNegative() {
        final CheckoutIntentCreateRequest r = valid().amount(new BigDecimal("-0.01"));
        assertThat(validator.validateProperty(r, "amount")).isNotEmpty();
    }
}
