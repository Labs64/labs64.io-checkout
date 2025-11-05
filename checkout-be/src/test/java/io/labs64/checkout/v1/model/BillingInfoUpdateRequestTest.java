package io.labs64.checkout.model;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.BillingInfoUpdateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BillingInfoUpdateRequestTest {

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

    private static BillingInfoUpdateRequest valid() {
        return new BillingInfoUpdateRequest().name("John Doe").email("john.doe@example.com").phone("+1234567890")
                .city("New York").country("US").address1("123 Main St").address2("Apt 4B").postalCode("10001")
                .state("NY").extra(Map.of("source", "web_portal"));
    }

    @Test
    void shouldFailWhenNameTooLong() {
        final BillingInfoUpdateRequest r = valid().name("x".repeat(maxNameLength + 1));
        assertThat(validator.validateProperty(r, "name")).isNotEmpty();
    }

    @Test
    void shouldFailWhenPhoneTooLong() {
        final BillingInfoUpdateRequest r = valid().phone("x".repeat(maxPhoneLength + 1));
        assertThat(validator.validateProperty(r, "phone")).isNotEmpty();
    }

    @Test
    void shouldFailWhenCityTooLong() {
        final BillingInfoUpdateRequest r = valid().city("x".repeat(maxCityLength + 1));
        assertThat(validator.validateProperty(r, "city")).isNotEmpty();
    }

    @Test
    void shouldFailWhenCountryTooLong() {
        final BillingInfoUpdateRequest r = valid().country("USA");
        assertThat(validator.validateProperty(r, "country")).isNotEmpty();
    }

    @Test
    void shouldFailWhenAddress1TooLong() {
        final BillingInfoUpdateRequest r = valid().address1("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(r, "address1")).isNotEmpty();
    }

    @Test
    void shouldFailWhenAddress2TooLong() {
        final BillingInfoUpdateRequest r = valid().address2("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(r, "address2")).isNotEmpty();
    }

    @Test
    void shouldFailWhenPostalCodeTooLong() {
        final BillingInfoUpdateRequest r = valid().postalCode("x".repeat(maxPostalCodeLength + 1));
        assertThat(validator.validateProperty(r, "postalCode")).isNotEmpty();
    }

    @Test
    void shouldFailWhenStateTooLong() {
        final BillingInfoUpdateRequest r = valid().state("x".repeat(maxStateLength + 1));
        assertThat(validator.validateProperty(r, "state")).isNotEmpty();
    }

    @Test
    void shouldFailWhenEmailHasInvalidFormat() {
        final BillingInfoUpdateRequest r = valid().email("not-an-email");
        assertThat(validator.validateProperty(r, "email")).isNotEmpty();
    }

    @Test
    void shouldFailWhenEmailTooLong() {
        final String longLocal = "a".repeat(260);
        final BillingInfoUpdateRequest r = valid().email(longLocal + "@example.com");
        assertThat(validator.validateProperty(r, "email")).isNotEmpty();
    }
}
