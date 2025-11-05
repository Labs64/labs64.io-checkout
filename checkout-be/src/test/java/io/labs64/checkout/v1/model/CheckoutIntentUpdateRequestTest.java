package io.labs64.checkout.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.CheckoutIntentUpdateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CheckoutIntentUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static CheckoutIntentUpdateRequest valid() {
        return new CheckoutIntentUpdateRequest().amount(14999L).currency("USD");
    }

    @Test
    void shouldPassWhenAmountIsNull() {
        final CheckoutIntentUpdateRequest r = valid().amount(null);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsZero() {
        final CheckoutIntentUpdateRequest r = valid().amount(0L);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsPositive() {
        final CheckoutIntentUpdateRequest r = valid().amount(1L);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldFailWhenAmountIsNegative() {
        final CheckoutIntentUpdateRequest r = valid().amount(-1L);
        assertThat(validator.validateProperty(r, "amount")).isNotEmpty();
    }
}
