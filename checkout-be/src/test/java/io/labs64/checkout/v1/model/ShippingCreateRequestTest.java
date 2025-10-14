package io.labs64.checkout.v1.model;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.v1.model.ShippingCreateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ShippingCreateRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static ShippingCreateRequest valid() {
        return new ShippingCreateRequest("Fedex", "RA123456789CN", UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    void shouldPassWhenExtraIsNull() {
        final ShippingCreateRequest r = valid().extra(null);
        assertThat(validator.validateProperty(r, "extra")).isEmpty();
    }
}
