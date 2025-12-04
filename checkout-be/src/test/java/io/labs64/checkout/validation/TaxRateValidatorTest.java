package io.labs64.checkout.validation;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import io.labs64.checkout.model.Tax;
import io.labs64.checkout.model.TaxRateType;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

/**
 * Unit tests for {@link TaxRateValidator}.
 */
@ExtendWith(MockitoExtension.class)
class TaxRateValidatorTest {
    private static final String DEFAULT_MESSAGE_TEMPLATE = "{jakarta.validation.constraints.DecimalMin.message}";
    private TaxRateValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder violationBuilder;

    @Mock
    private NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        validator = new TaxRateValidator();

        // configure fluent API chain: context -> violationBuilder -> nodeBuilder
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        lenient().when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        lenient().when(nodeBuilder.addConstraintViolation()).thenReturn(context);
        lenient().when(context.getDefaultConstraintMessageTemplate()).thenReturn(DEFAULT_MESSAGE_TEMPLATE);
    }

    @Test
    void nullValueIsValid() {
        final boolean result = validator.isValid(null, context);

        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void nullRateTypeIsValid() {
        final Tax tax = mock(Tax.class);
        when(tax.getRateType()).thenReturn(null);
        when(tax.getRate()).thenReturn(BigDecimal.ONE);

        final boolean result = validator.isValid(tax, context);

        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void nullRateIsValid() {
        final Tax tax = mock(Tax.class);
        when(tax.getRateType()).thenReturn(TaxRateType.FIXED);
        when(tax.getRate()).thenReturn(null);

        final boolean result = validator.isValid(tax, context);

        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void negativeRateIsInvalid() {
        final Tax tax = mock(Tax.class);
        when(tax.getRateType()).thenReturn(TaxRateType.FIXED);
        when(tax.getRate()).thenReturn(BigDecimal.valueOf(-1));

        final boolean result = validator.isValid(tax, context);

        assertFalse(result);
        // for non-null rateType & rate validator always disables default violations
        verify(context).disableDefaultConstraintViolation();
        // and adds violation on "rate"
        verify(context).buildConstraintViolationWithTemplate(anyString());
        verify(violationBuilder).addPropertyNode("rate");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void fixedRateWithFractionalPartIsInvalid() {
        final Tax tax = mock(Tax.class);
        when(tax.getRateType()).thenReturn(TaxRateType.FIXED);
        when(tax.getRate()).thenReturn(new BigDecimal("10.50")); // scale = 2

        final boolean result = validator.isValid(tax, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(anyString());
        verify(violationBuilder).addPropertyNode("rate");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void fixedRateWithoutFractionalPartIsValid() {
        final Tax tax = mock(Tax.class);
        when(tax.getRateType()).thenReturn(TaxRateType.FIXED);
        when(tax.getRate()).thenReturn(BigDecimal.TEN); // scale = 0

        final boolean result = validator.isValid(tax, context);

        assertTrue(result);
        // still called for non-null rateType & rate
        verify(context).disableDefaultConstraintViolation();
        // but no violations should be added
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
}
