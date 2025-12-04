package io.labs64.checkout.validation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintViolationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Unit tests for ValidExtraValidator. Focus: size limits, key rules, allowed value types/lengths, nested structures,
 * NaN/Infinity, and null handling.
 */
@ExtendWith(MockitoExtension.class)
class ExtraValidatorTest {

    @Mock
    ConstraintValidatorContext ctx;
    @Mock
    HibernateConstraintValidatorContext hv;
    @Mock
    HibernateConstraintViolationBuilder hvBuilder;

    private void stubViolationChain() {
        when(ctx.unwrap(HibernateConstraintValidatorContext.class)).thenReturn(hv);
        when(hv.buildConstraintViolationWithTemplate(anyString())).thenReturn(hvBuilder);
        when(hvBuilder.addConstraintViolation()).thenReturn(hv);
    }

    private ExtraValidator newValidator(final int maxEntries, final int maxKeyLength, final String keyPattern,
            final boolean allowString, final boolean allowNumber, final boolean allowBoolean,
            final int maxStringValueLength) {
        final ValidExtra ann = mock(ValidExtra.class);
        when(ann.maxEntries()).thenReturn(maxEntries);
        when(ann.maxKeyLength()).thenReturn(maxKeyLength);
        when(ann.keyPattern()).thenReturn(keyPattern);
        when(ann.allowString()).thenReturn(allowString);
        when(ann.allowNumber()).thenReturn(allowNumber);
        when(ann.allowBoolean()).thenReturn(allowBoolean);
        when(ann.maxStringValueLength()).thenReturn(maxStringValueLength);

        final ExtraValidator v = new ExtraValidator();
        v.initialize(ann);
        return v;
    }

    @Test
    void shouldPassWhenTargetIsNull() {
        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 255);
        assertThat(validator.isValid(null, ctx)).isTrue();
        verifyNoInteractions(hv);
    }

    @Test
    void shouldFailWhenTargetIsNotMap() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 255);

        final boolean valid = validator.isValid("not-a-map", ctx);

        assertThat(valid).isFalse();
        verify(ctx).disableDefaultConstraintViolation();
        verify(hv).buildConstraintViolationWithTemplate("{validation.map.must_be_map}");
        verify(hvBuilder).addConstraintViolation();
    }

    @Test
    void shouldFailWhenTooManyEntries() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(1, 50, "", true, true, true, 255);
        final Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("a", 1);
        extra.put("b", 2);

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(ctx).disableDefaultConstraintViolation();

        final ArgumentCaptor<String> template = ArgumentCaptor.forClass(String.class);
        verify(hv).buildConstraintViolationWithTemplate(template.capture());
        assertThat(template.getValue()).isEqualTo("validation.extra.too_many_entries");
        verify(hv).addMessageParameter(eq("max"), eq(1));
        verify(hvBuilder).addConstraintViolation();
    }

    @Test
    void shouldFailWhenKeyIsNotString() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 255);
        final Map<Object, Object> extra = new LinkedHashMap<>();
        extra.put(123, "x");

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(hv).addMessageParameter("key", 123);
        verify(hv).buildConstraintViolationWithTemplate("{validation.extra.key.must_be_string}");
    }

    @Test
    void shouldFailWhenKeyIsBlank() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 255);
        final Map<String, Object> extra = Map.of("   ", "x");

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        final ArgumentCaptor<String> template = ArgumentCaptor.forClass(String.class);
        verify(hv, atLeastOnce()).buildConstraintViolationWithTemplate(template.capture());
        assertThat(template.getAllValues()).contains("{validation.extra.key.blank}");
    }

    @Test
    void shouldFailWhenKeyTooLong() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 3, "", true, true, true, 255);
        final Map<String, Object> extra = Map.of("abcd", "x");

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(hv).addMessageParameter("key", "abcd");
        verify(hv).addMessageParameter("max", 3);
        verify(hv).buildConstraintViolationWithTemplate("validation.extra.key.length");
    }

    @Test
    void shouldFailWhenKeyDoesNotMatchPattern() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "^[a-z_]+$", true, true, true, 255);
        final Map<String, Object> extra = Map.of("Bad-Key", "x");

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(hv).buildConstraintViolationWithTemplate("{validation.extra.key.pattern}");
    }

    @Test
    void shouldFailWhenValueIsNull() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 255);
        final Map<String, Object> extra = new HashMap<>();
        extra.put("k", null);

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(hv).addMessageParameter("key", "k");
        verify(hv).buildConstraintViolationWithTemplate("{validation.extra.value.not_null}");
    }

    @Test
    void shouldFailWhenStringValueTooLong() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, false, false, 3);
        final Map<String, Object> extra = Map.of("k", "abcd");

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(hv).addMessageParameter("key", "k");
        verify(hv).addMessageParameter("max", 3);
        verify(hv).buildConstraintViolationWithTemplate("{validation.extra.value.length}");
    }

    @Test
    void shouldFailWhenNestedStructureProvided() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 255);
        final Map<String, Object> extra = Map.of("k", Map.of("nested", 1));

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isFalse();
        verify(hv).buildConstraintViolationWithTemplate("{validation.extra.value.type}");
    }

    @Test
    void shouldFailWhenNumberIsNaNOrInfinity() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", false, true, false, 255);

        assertThat(validator.isValid(Map.of("nan", Double.NaN), ctx)).isFalse();
        assertThat(validator.isValid(Map.of("inf", Double.POSITIVE_INFINITY), ctx)).isFalse();

        verify(hv, atLeast(2)).buildConstraintViolationWithTemplate("{validation.extra.value.type}");
    }

    @Test
    void shouldPassWhenAllowedTypesAndLengthsOk() {
        final ExtraValidator validator = newValidator(10, 50, "", true, true, true, 10);
        final Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("s", "ok");
        extra.put("n", 42);
        extra.put("b", Boolean.TRUE);

        final boolean valid = validator.isValid(extra, ctx);

        assertThat(valid).isTrue();
        verify(hv, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void shouldFailWhenValueTypeNotAllowed() {
        stubViolationChain();

        final ExtraValidator validator = newValidator(10, 50, "", true, false, false, 10);

        final boolean valid = validator.isValid(Map.of("n", 1), ctx);

        assertThat(valid).isFalse();
        verify(hv).buildConstraintViolationWithTemplate("{validation.extra.value.type}");
    }
}
