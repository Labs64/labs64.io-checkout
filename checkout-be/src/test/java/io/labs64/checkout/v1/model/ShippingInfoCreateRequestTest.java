package io.labs64.checkout.v1.model;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.v1.model.ShippingInfoCreateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ShippingInfoCreateRequestTest {

    private static Validator validator;

    private static final int maxNameLength = 255;
    private static final int maxPhoneLength = 50;
    private static final int maxCityLength = 100;
    private static final int maxAddressLength = 255;
    private static final int maxPostalCodeLength = 20;
    private static final int maxStateLength = 50;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    private static ShippingInfoCreateRequest valid() {
        return new ShippingInfoCreateRequest("John Doe").phone("+15687985466").city("New York").country("US")
                .address1("123 Main St").address2("Apt 4B").postalCode("10001").state("NY")
                .extra(Map.of("source", "web_portal")).confirm(Boolean.TRUE);
    }

    @Test
    void shouldPassWhenNameAtMax() {
        final ShippingInfoCreateRequest r = valid().name("x".repeat(maxNameLength));
        assertThat(validator.validateProperty(r, "name")).isEmpty();
    }

    @Test
    void shouldFailWhenNameTooLong() {
        final ShippingInfoCreateRequest r = valid().name("x".repeat(maxNameLength + 1));
        assertThat(validator.validateProperty(r, "name")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPhoneAtMax() {
        final ShippingInfoCreateRequest r = valid().phone("x".repeat(maxPhoneLength));
        assertThat(validator.validateProperty(r, "phone")).isEmpty();
    }

    @Test
    void shouldFailWhenPhoneTooLong() {
        final ShippingInfoCreateRequest r = valid().phone("x".repeat(maxPhoneLength + 1));
        assertThat(validator.validateProperty(r, "phone")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCityAtMax() {
        final ShippingInfoCreateRequest r = valid().city("x".repeat(maxCityLength));
        assertThat(validator.validateProperty(r, "city")).isEmpty();
    }

    @Test
    void shouldFailWhenCityTooLong() {
        final ShippingInfoCreateRequest r = valid().city("x".repeat(maxCityLength + 1));
        assertThat(validator.validateProperty(r, "city")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCountryAtMax() {
        final ShippingInfoCreateRequest r = valid().country("US");
        assertThat(validator.validateProperty(r, "country")).isEmpty();
    }

    @Test
    void shouldFailWhenCountryTooLong() {
        final ShippingInfoCreateRequest r = valid().country("USA");
        assertThat(validator.validateProperty(r, "country")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress1AtMax() {
        final ShippingInfoCreateRequest r = valid().address1("x".repeat(maxAddressLength));
        assertThat(validator.validateProperty(r, "address1")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress1TooLong() {
        final ShippingInfoCreateRequest r = valid().address1("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(r, "address1")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress2AtMax() {
        final ShippingInfoCreateRequest r = valid().address2("x".repeat(maxAddressLength));
        assertThat(validator.validateProperty(r, "address2")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress2TooLong() {
        final ShippingInfoCreateRequest r = valid().address2("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(r, "address2")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPostalCodeAtMax() {
        final ShippingInfoCreateRequest r = valid().postalCode("x".repeat(maxPostalCodeLength));
        assertThat(validator.validateProperty(r, "postalCode")).isEmpty();
    }

    @Test
    void shouldFailWhenPostalCodeTooLong() {
        final ShippingInfoCreateRequest r = valid().postalCode("x".repeat(maxPostalCodeLength + 1));
        assertThat(validator.validateProperty(r, "postalCode")).isNotEmpty();
    }

    @Test
    void shouldPassWhenStateAtMax() {
        final ShippingInfoCreateRequest r = valid().state("x".repeat(maxStateLength));
        assertThat(validator.validateProperty(r, "state")).isEmpty();
    }

    @Test
    void shouldFailWhenStateTooLong() {
        final ShippingInfoCreateRequest r = valid().state("x".repeat(maxStateLength + 1));
        assertThat(validator.validateProperty(r, "state")).isNotEmpty();
    }

    @Test
    void shouldPassWhenConfirmNullOrFalse() {
        assertThat(validator.validate(new ShippingInfoCreateRequest("John").confirm(null))).isEmpty();
        assertThat(validator.validate(new ShippingInfoCreateRequest("John").confirm(Boolean.FALSE))).isEmpty();
        assertThat(validator.validate(new ShippingInfoCreateRequest("John").confirm(Boolean.TRUE))).isEmpty();
    }
}
