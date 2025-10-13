package io.labs64.checkout.v1.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.v1.model.ShippingInfoUpdateRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ShippingInfoUpdateRequestTest {

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

    private static ShippingInfoUpdateRequest valid() {
        return new ShippingInfoUpdateRequest()
                .name("John Doe")
                .phone("+15687985466")
                .city("New York")
                .country("US")
                .address1("123 Main St")
                .address2("Apt 4B")
                .postalCode("10001")
                .state("NY")
                .extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldPassWhenNameAtMax() {
        final ShippingInfoUpdateRequest r = valid().name("x".repeat(maxNameLength));
        assertThat(validator.validateProperty(r, "name")).isEmpty();
    }

    @Test
    void shouldFailWhenNameTooLong() {
        final ShippingInfoUpdateRequest r = valid().name("x".repeat(maxNameLength + 1));
        assertThat(validator.validateProperty(r, "name")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPhoneAtMax() {
        final ShippingInfoUpdateRequest r = valid().phone("x".repeat(maxPhoneLength));
        assertThat(validator.validateProperty(r, "phone")).isEmpty();
    }

    @Test
    void shouldFailWhenPhoneTooLong() {
        final ShippingInfoUpdateRequest r = valid().phone("x".repeat(maxPhoneLength + 1));
        assertThat(validator.validateProperty(r, "phone")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCityAtMax() {
        final ShippingInfoUpdateRequest r = valid().city("x".repeat(maxCityLength));
        assertThat(validator.validateProperty(r, "city")).isEmpty();
    }

    @Test
    void shouldFailWhenCityTooLong() {
        final ShippingInfoUpdateRequest r = valid().city("x".repeat(maxCityLength + 1));
        assertThat(validator.validateProperty(r, "city")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCountryAtMax() {
        final ShippingInfoUpdateRequest r = valid().country("US");
        assertThat(validator.validateProperty(r, "country")).isEmpty();
    }

    @Test
    void shouldFailWhenCountryTooLong() {
        final ShippingInfoUpdateRequest r = valid().country("USA");
        assertThat(validator.validateProperty(r, "country")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress1AtMax() {
        final ShippingInfoUpdateRequest r = valid().address1("x".repeat(maxAddressLength));
        assertThat(validator.validateProperty(r, "address1")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress1TooLong() {
        final ShippingInfoUpdateRequest r = valid().address1("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(r, "address1")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress2AtMax() {
        final ShippingInfoUpdateRequest r = valid().address2("x".repeat(maxAddressLength));
        assertThat(validator.validateProperty(r, "address2")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress2TooLong() {
        final ShippingInfoUpdateRequest r = valid().address2("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(r, "address2")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPostalCodeAtMax() {
        final ShippingInfoUpdateRequest r = valid().postalCode("x".repeat(maxPostalCodeLength));
        assertThat(validator.validateProperty(r, "postalCode")).isEmpty();
    }

    @Test
    void shouldFailWhenPostalCodeTooLong() {
        final ShippingInfoUpdateRequest r = valid().postalCode("x".repeat(maxPostalCodeLength + 1));
        assertThat(validator.validateProperty(r, "postalCode")).isNotEmpty();
    }

    @Test
    void shouldPassWhenStateAtMax() {
        final ShippingInfoUpdateRequest r = valid().state("x".repeat(maxStateLength));
        assertThat(validator.validateProperty(r, "state")).isEmpty();
    }

    @Test
    void shouldFailWhenStateTooLong() {
        final ShippingInfoUpdateRequest r = valid().state("x".repeat(maxStateLength + 1));
        assertThat(validator.validateProperty(r, "state")).isNotEmpty();
    }
}
