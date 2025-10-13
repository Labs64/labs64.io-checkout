package io.labs64.checkout.v1.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;

import io.labs64.checkout.v1.model.CheckoutIntentUpdateRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutIntentUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static CheckoutIntentUpdateRequest valid() {
        return new CheckoutIntentUpdateRequest()
                .amount(new BigDecimal("149.99"))
                .currency("USD");
    }

    @Test
    void shouldPassWhenAmountIsNull() {
        final CheckoutIntentUpdateRequest r = valid().amount(null);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsZero() {
        final CheckoutIntentUpdateRequest r = valid().amount(BigDecimal.ZERO);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsPositive() {
        final CheckoutIntentUpdateRequest r = valid().amount(new BigDecimal("0.01"));
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldFailWhenAmountIsNegative() {
        final CheckoutIntentUpdateRequest r = valid().amount(new BigDecimal("-0.01"));
        assertThat(validator.validateProperty(r, "amount")).isNotEmpty();
    }
}
