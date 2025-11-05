package io.labs64.checkout.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.CheckoutIntentCreateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CheckoutIntentCreateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static io.labs64.checkout.model.CheckoutIntentCreateRequest valid() {
        return new CheckoutIntentCreateRequest(14999L, "USD", "STRIPE");
    }

    @Test
    void shouldPassWhenAmountIsZero() {
        final CheckoutIntentCreateRequest r = valid().amount(0L);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldPassWhenAmountIsPositive() {
        final CheckoutIntentCreateRequest r = valid().amount(1L);
        assertThat(validator.validateProperty(r, "amount")).isEmpty();
    }

    @Test
    void shouldFailWhenAmountIsNegative() {
        final CheckoutIntentCreateRequest r = valid().amount(-1L);
        assertThat(validator.validateProperty(r, "amount")).isNotEmpty();
    }
}
