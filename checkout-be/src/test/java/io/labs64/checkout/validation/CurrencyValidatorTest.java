package io.labs64.checkout.validation;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.labs64.checkout.config.CheckoutProperties;
import io.labs64.checkout.messages.ValidationMessages;
import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class CurrencyValidatorTest {

    @Mock
    ValidationMessages msg;
    @Mock
    CheckoutProperties props;

    @Mock
    ConstraintValidatorContext ctx;
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private CurrencyValidator validator;

    @BeforeEach
    void setUp() {
        when(props.getCurrency()).thenReturn(List.of(" usd ", "", "Eur"));
        validator = new CurrencyValidator(msg, props);
    }

    @Test
    void shouldBuildAllowedSetFromPropertiesWithTrimUpperAndIgnoreNullEmpty() {
        assertThat(validator.allowed).containsExactlyInAnyOrder("USD", "EUR");
    }

    @Test
    void shouldPassWhenTargetIsNullOrEmpty() {
        assertThat(validator.isValid(null, ctx)).isTrue();
        assertThat(validator.isValid("", ctx)).isTrue();
        verifyNoInteractions(ctx);
    }

    @Test
    void shouldPassWhenCurrencyIsAllowedCaseInsensitiveAndTrimmed() {
        assertThat(validator.isValid("eur", ctx)).isTrue();
        assertThat(validator.isValid("  eur  ", ctx)).isTrue();
        assertThat(validator.isValid("USD", ctx)).isTrue();
        verifyNoInteractions(ctx);
    }

    @Test
    void shouldFailAndSetCustomMessageWhenCurrencyNotAllowed() {
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(ctx);

        when(msg.invalidCurrency(any())).thenReturn("invalid currency");

        final boolean valid = validator.isValid("jpy", ctx);

        assertThat(valid).isFalse();

        verify(ctx).disableDefaultConstraintViolation();

        final ArgumentCaptor<String> template = ArgumentCaptor.forClass(String.class);
        verify(ctx).buildConstraintViolationWithTemplate(template.capture());
        assertThat(template.getValue()).isEqualTo("invalid currency");

        final ArgumentCaptor<Set<String>> setCaptor = ArgumentCaptor.forClass(Set.class);
        verify(msg).invalidCurrency(setCaptor.capture());
        assertThat(setCaptor.getValue()).containsExactlyInAnyOrderElementsOf(validator.allowed);

        verify(builder).addConstraintViolation();
    }
}
