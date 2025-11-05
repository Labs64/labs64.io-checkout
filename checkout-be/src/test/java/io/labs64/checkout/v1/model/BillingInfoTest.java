package io.labs64.checkout.model;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.BillingInfo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BillingInfoTest {

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
        try (final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();) {
            validator = factory.getValidator();
        }
    }

    private static BillingInfo validBillingInfo() {
        return new BillingInfo().name("John Doe").email("john.doe@example.com").phone("+1234567890").city("New York")
                .country("US").address1("123 Main St").address2("Apt 4B").postalCode("10001").state("NY")
                .extra(Map.of("source", "web_portal", "promoCode", "SUMMER2025"));
    }

    @Test
    void shouldPassWhenValidObject() {
        final BillingInfo info = validBillingInfo();
        final Set<ConstraintViolation<BillingInfo>> violations = validator.validate(info);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenOptionalFieldsAreNull() {
        final BillingInfo info = validBillingInfo().email(null).phone(null).city(null).address1(null).address2(null)
                .postalCode(null).state(null).country(null);
        assertThat(validator.validate(info)).isEmpty();
    }

    @Test
    void shouldPassWhenNameHasMaxLength() {
        final BillingInfo info = validBillingInfo().name("x".repeat(maxNameLength));
        assertThat(validator.validateProperty(info, "name")).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsTooLong() {
        final BillingInfo info = validBillingInfo().name("x".repeat(maxNameLength + 1));
        assertThat(validator.validateProperty(info, "name")).isNotEmpty();
    }

    @Test
    void shouldFailWhenEmailHasInvalidFormat() {
        final BillingInfo info = validBillingInfo().email("not-an-email");
        assertThat(validator.validateProperty(info, "email")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPhoneHasMaxLength() {
        final BillingInfo info = validBillingInfo().phone("x".repeat(maxPhoneLength));
        assertThat(validator.validateProperty(info, "phone")).isEmpty();
    }

    @Test
    void shouldFailWhenPhoneIsTooLong() {
        final BillingInfo info = validBillingInfo().phone("x".repeat(maxPhoneLength + 1));
        assertThat(validator.validateProperty(info, "phone")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCountryHasMaxLength() {
        final BillingInfo info = validBillingInfo().country("US");
        assertThat(validator.validateProperty(info, "country")).isEmpty();
    }

    @Test
    void shouldFailWhenCountryIsTooLong() {
        final BillingInfo info = validBillingInfo().country("USA");
        assertThat(validator.validateProperty(info, "country")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCityHasMaxLength() {
        final BillingInfo info = validBillingInfo().city("x".repeat(maxCityLength));
        assertThat(validator.validateProperty(info, "city")).isEmpty();
    }

    @Test
    void shouldFailWhenCityIsTooLong() {
        final BillingInfo info = validBillingInfo().city("x".repeat(maxCityLength + 1));
        assertThat(validator.validateProperty(info, "city")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress1HasMaxLength() {
        final BillingInfo info = validBillingInfo().address1("x".repeat(maxAddress1Length));
        assertThat(validator.validateProperty(info, "address1")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress1IsTooLong() {
        final BillingInfo info = validBillingInfo().address1("x".repeat(maxAddress1Length + 1));
        assertThat(validator.validateProperty(info, "address1")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress2HasMaxLength() {
        final BillingInfo info = validBillingInfo().address2("x".repeat(maxAddress2Length));
        assertThat(validator.validateProperty(info, "address2")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress2IsTooLong() {
        final BillingInfo info = validBillingInfo().address2("x".repeat(maxAddress2Length + 1));
        assertThat(validator.validateProperty(info, "address2")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPostalCodeHasMaxLength() {
        final BillingInfo info = validBillingInfo().postalCode("x".repeat(maxPostalCodeLength));
        assertThat(validator.validateProperty(info, "postalCode")).isEmpty();
    }

    @Test
    void shouldFailWhenPostalCodeIsTooLong() {
        final BillingInfo info = validBillingInfo().postalCode("x".repeat(maxPostalCodeLength + 1));
        assertThat(validator.validateProperty(info, "postalCode")).isNotEmpty();
    }

    @Test
    void shouldPassWhenStateHasMaxLength() {
        final BillingInfo info = validBillingInfo().state("x".repeat(maxStateLength));
        assertThat(validator.validateProperty(info, "state")).isEmpty();
    }

    @Test
    void shouldFailWhenStateIsTooLong() {
        final BillingInfo info = validBillingInfo().state("x".repeat(maxStateLength + 1));
        assertThat(validator.validateProperty(info, "state")).isNotEmpty();
    }
}
