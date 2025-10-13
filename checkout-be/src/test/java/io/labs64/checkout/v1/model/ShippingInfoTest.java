package io.labs64.checkout.v1.model;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import io.labs64.checkout.v1.model.ShippingInfo;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ShippingInfoTest {

    private static Validator validator;

    private final static int maxNameLength = 255;
    private final static int maxPhoneLength = 50;
    private final static int maxCityLength = 100;
    private final static int maxAddress1Length = 255;
    private final static int maxAddress2Length = 255;
    private final static int maxPostalCodeLength = 20;
    private final static int maxStateLength = 50;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private static ShippingInfo validShippingInfo() {
        return new ShippingInfo().name("John Doe").phone("+15687985466").city("New York").country("US")
                .address1("123 Main St").address2("Apt 4B").postalCode("10001").state("NY")
                .extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldBeEqualAndHashEqualWhenAllFieldsSame() {
        final ShippingInfo a = validShippingInfo().id(UUID.randomUUID()).createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now());
        final ShippingInfo b = validShippingInfo().id(a.getId()).createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt());

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void shouldPassWhenAllFieldsValid() {
        assertThat(validator.validate(validShippingInfo())).isEmpty();
    }

    @Test
    void shouldPassWhenOptionalFieldsAreNull() {
        final ShippingInfo info = validShippingInfo().name(null).phone(null).city(null).country(null).address1(null)
                .address2(null).postalCode(null).state(null);
        assertThat(validator.validate(info)).isEmpty();
    }

    @Test
    void shouldPassWhenNameHasMaxLength() {
        final ShippingInfo info = validShippingInfo().name("x".repeat(maxNameLength));
        assertThat(validator.validateProperty(info, "name")).isEmpty();
    }

    @Test
    void shouldFailWhenNameTooLong() {
        final ShippingInfo info = validShippingInfo().name("x".repeat(maxNameLength + 1));
        assertThat(validator.validateProperty(info, "name")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPhoneHasMaxLength() {
        final ShippingInfo info = validShippingInfo().phone("x".repeat(maxPhoneLength));
        assertThat(validator.validateProperty(info, "phone")).isEmpty();
    }

    @Test
    void shouldFailWhenPhoneTooLong() {
        final ShippingInfo info = validShippingInfo().phone("x".repeat(maxPhoneLength + 1));
        assertThat(validator.validateProperty(info, "phone")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCityHasMaxLength() {
        final ShippingInfo info = validShippingInfo().city("x".repeat(maxCityLength));
        assertThat(validator.validateProperty(info, "city")).isEmpty();
    }

    @Test
    void shouldFailWhenCityTooLong() {
        final ShippingInfo info = validShippingInfo().city("x".repeat(maxCityLength + 1));
        assertThat(validator.validateProperty(info, "city")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCountryHasTwoLetters() {
        final ShippingInfo info = validShippingInfo().country("US");
        assertThat(validator.validateProperty(info, "country")).isEmpty();
    }

    @Test
    void shouldFailWhenCountryTooLong() {
        final ShippingInfo info = validShippingInfo().country("USA");
        assertThat(validator.validateProperty(info, "country")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress1HasMaxLength() {
        final ShippingInfo info = validShippingInfo().address1("x".repeat(maxAddress1Length));
        assertThat(validator.validateProperty(info, "address1")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress1TooLong() {
        final ShippingInfo info = validShippingInfo().address1("x".repeat(maxAddress1Length + 1));
        assertThat(validator.validateProperty(info, "address1")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress2HasMaxLength() {
        final ShippingInfo info = validShippingInfo().address2("x".repeat(maxAddress2Length));
        assertThat(validator.validateProperty(info, "address2")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress2TooLong() {
        final ShippingInfo info = validShippingInfo().address2("x".repeat(maxAddress2Length + 1));
        assertThat(validator.validateProperty(info, "address2")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPostalCodeHasMaxLength() {
        final ShippingInfo info = validShippingInfo().postalCode("x".repeat(maxPostalCodeLength));
        assertThat(validator.validateProperty(info, "postalCode")).isEmpty();
    }

    @Test
    void shouldFailWhenPostalCodeTooLong() {
        final ShippingInfo info = validShippingInfo().postalCode("x".repeat(maxPostalCodeLength + 1));
        assertThat(validator.validateProperty(info, "postalCode")).isNotEmpty();
    }

    @Test
    void shouldPassWhenStateHasMaxLength() {
        final ShippingInfo info = validShippingInfo().state("x".repeat(maxStateLength));
        assertThat(validator.validateProperty(info, "state")).isEmpty();
    }

    @Test
    void shouldFailWhenStateTooLong() {
        final ShippingInfo info = validShippingInfo().state("x".repeat(maxStateLength + 1));
        assertThat(validator.validateProperty(info, "state")).isNotEmpty();
    }
}
