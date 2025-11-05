package io.labs64.checkout.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import io.labs64.checkout.model.BillingInfoCreateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BillingInfoCreateRequestTest {

    private static Validator validator;

    private static final int maxNameLength = 255;
    private static final int maxPhoneLength = 50;
    private static final int maxCityLength = 100;
    private static final int maxAddressLength = 255;
    private static final int maxPostalCodeLength = 20;
    private static final int maxStateLength = 50;

    @BeforeAll
    static void setupValidator() {
        try (final ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private static BillingInfoCreateRequest validRequest() {
        return new BillingInfoCreateRequest("John Doe").email("john.doe@example.com").phone("+1234567890")
                .city("New York").country("US").address1("123 Main St").address2("Apt 4B").postalCode("10001")
                .state("NY").extra(Map.of("source", "web_portal", "promoCode", "SUMMER2025")).confirm(Boolean.TRUE);
    }

    @Test
    void shouldPassWhenValidObject() {
        final BillingInfoCreateRequest req = validRequest();
        final Set<ConstraintViolation<BillingInfoCreateRequest>> v = validator.validate(req);
        assertThat(v).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsNull() {
        final BillingInfoCreateRequest req = validRequest().name(null);
        assertThat(validator.validateProperty(req, "name")).isNotEmpty();
    }

    @Test
    void shouldPassWhenNameHasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().name("x".repeat(maxNameLength));
        assertThat(validator.validateProperty(req, "name")).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsTooLong() {
        final BillingInfoCreateRequest req = validRequest().name("x".repeat(maxNameLength + 1));
        assertThat(validator.validateProperty(req, "name")).isNotEmpty();
    }

    @Test
    void shouldFailWhenEmailHasInvalidFormat() {
        final BillingInfoCreateRequest req = validRequest().email("not-an-email");
        assertThat(validator.validateProperty(req, "email")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCountryHasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().country("US");
        assertThat(validator.validateProperty(req, "country")).isEmpty();
    }

    @Test
    void shouldFailWhenCountryIsTooLong() {
        final BillingInfoCreateRequest req = validRequest().country("USA");
        assertThat(validator.validateProperty(req, "country")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPhoneHasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().phone("x".repeat(maxPhoneLength));
        assertThat(validator.validateProperty(req, "phone")).isEmpty();
    }

    @Test
    void shouldFailWhenPhoneIsTooLong() {
        final BillingInfoCreateRequest req = validRequest().phone("x".repeat(maxPhoneLength + 1));
        assertThat(validator.validateProperty(req, "phone")).isNotEmpty();
    }

    @Test
    void shouldPassWhenCityHasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().city("x".repeat(maxCityLength));
        assertThat(validator.validateProperty(req, "city")).isEmpty();
    }

    @Test
    void shouldFailWhenCityIsTooLong() {
        final BillingInfoCreateRequest req = validRequest().city("x".repeat(maxCityLength + 1));
        assertThat(validator.validateProperty(req, "city")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress1HasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().address1("x".repeat(maxAddressLength));
        assertThat(validator.validateProperty(req, "address1")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress1IsTooLong() {
        final BillingInfoCreateRequest req = validRequest().address1("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(req, "address1")).isNotEmpty();
    }

    @Test
    void shouldPassWhenAddress2HasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().address2("x".repeat(maxAddressLength));
        assertThat(validator.validateProperty(req, "address2")).isEmpty();
    }

    @Test
    void shouldFailWhenAddress2IsTooLong() {
        final BillingInfoCreateRequest req = validRequest().address2("x".repeat(maxAddressLength + 1));
        assertThat(validator.validateProperty(req, "address2")).isNotEmpty();
    }

    @Test
    void shouldPassWhenPostalCodeHasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().postalCode("x".repeat(maxPostalCodeLength));
        assertThat(validator.validateProperty(req, "postalCode")).isEmpty();
    }

    @Test
    void shouldFailWhenPostalCodeIsTooLong() {
        final BillingInfoCreateRequest req = validRequest().postalCode("x".repeat(maxPostalCodeLength + 1));
        assertThat(validator.validateProperty(req, "postalCode")).isNotEmpty();
    }

    @Test
    void shouldPassWhenStateHasMaxLength() {
        final BillingInfoCreateRequest req = validRequest().state("x".repeat(maxStateLength));
        assertThat(validator.validateProperty(req, "state")).isEmpty();
    }

    @Test
    void shouldFailWhenStateIsTooLong() {
        final BillingInfoCreateRequest req = validRequest().state("x".repeat(maxStateLength + 1));
        assertThat(validator.validateProperty(req, "state")).isNotEmpty();
    }

    @Test
    void shouldPassWhenExtraHasAllowedTypes() {
        final Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("s", "ok");
        ok.put("n", 42);
        ok.put("b", Boolean.TRUE);
        final BillingInfoCreateRequest req = validRequest().extra(ok);
        assertThat(validator.validateProperty(req, "extra")).isEmpty();
    }

    @Test
    void shouldFailWhenExtraHasDisallowedTypes() {
        final Map<String, Object> bad = new LinkedHashMap<>();
        bad.put("map", Map.of("nested", "nope"));
        final BillingInfoCreateRequest req = validRequest().extra(bad);
        assertThat(validator.validateProperty(req, "extra")).isNotEmpty();
    }

    @Test
    void shouldPassWhenConfirmIsNullOrFalse() {
        assertThat(validator.validate(new BillingInfoCreateRequest("John").confirm(null))).isEmpty();
        assertThat(validator.validate(new BillingInfoCreateRequest("John").confirm(Boolean.FALSE))).isEmpty();
    }

    @Test
    void shouldUseRequiredArgsConstructorForName() {
        final BillingInfoCreateRequest req = new BillingInfoCreateRequest("John");
        assertThat(req.getName()).isEqualTo("John");
    }
}
