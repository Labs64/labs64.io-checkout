package io.labs64.checkout.v1.model;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.v1.model.ShippingUpdateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ShippingUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static ShippingUpdateRequest valid() {
        return new ShippingUpdateRequest().carrier("Fedex").trackingNumber("RA123456789CN")
                .shippingInfoId(java.util.UUID.randomUUID()).checkoutIntentId(java.util.UUID.randomUUID())
                .extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldPassWhenExtraIsNull() {
        final ShippingUpdateRequest r = valid().extra(null);
        assertThat(validator.validateProperty(r, "extra")).isEmpty();
    }
}
