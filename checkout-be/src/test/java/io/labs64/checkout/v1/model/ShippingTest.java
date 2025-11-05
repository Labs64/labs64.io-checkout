package io.labs64.checkout.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.Shipping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ShippingTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static Shipping validShipping() {
        return new Shipping().carrier("Fedex").trackingNumber("RA123456789CN").shippingInfoId(UUID.randomUUID())
                .checkoutIntentId(UUID.randomUUID()).extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldPassWhenOptionalFieldsAreNull() {
        final Shipping shipping = validShipping().carrier(null).trackingNumber(null).shippingInfoId(null)
                .checkoutIntentId(null).extra(null);
        assertThat(validator.validate(shipping)).isEmpty();
    }

    @Test
    void shouldFailWhenExtraHasNestedStructure_ifValidExtraEnabled() {
        final boolean hasConstraint = validator.getConstraintsForClass(Shipping.class)
                .getConstraintsForProperty("extra") != null
                && !validator.getConstraintsForClass(Shipping.class).getConstraintsForProperty("extra")
                        .getConstraintDescriptors().isEmpty();

        Assumptions.assumeTrue(hasConstraint, "ValidExtra not active; skipping.");

        final Shipping shipping = validShipping().extra(Map.of("nested", Map.of("x", 1)));
        assertThat(validator.validateProperty(shipping, "extra")).isNotEmpty();
    }

    @Test
    void shouldHaveIsoDateTimeOnTimestamps() throws Exception {
        for (final String f : List.of("createdAt", "updatedAt")) {
            final Field field = Shipping.class.getDeclaredField(f);
            final DateTimeFormat dtf = field.getAnnotation(DateTimeFormat.class);
            assertThat(dtf).isNotNull();
            assertThat(dtf.iso()).isEqualTo(ISO.DATE_TIME);
        }
    }

    @Test
    void shouldHaveReadOnlySchemaOnIdAndTimestamps() throws Exception {
        for (final String getter : List.of("getId", "getCreatedAt", "getUpdatedAt")) {
            final Method m = Shipping.class.getMethod(getter);
            final Schema sch = m.getAnnotation(Schema.class);
            assertThat(sch).isNotNull();
            assertThat(sch.accessMode()).isEqualTo(Schema.AccessMode.READ_ONLY);
        }
    }
}
