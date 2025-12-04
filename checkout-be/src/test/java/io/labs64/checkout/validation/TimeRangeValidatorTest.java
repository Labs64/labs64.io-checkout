package io.labs64.checkout.validation;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import lombok.Getter;
import lombok.Setter;

@ExtendWith(MockitoExtension.class)
class TimeRangeValidatorTest {

    private static final String DEFAULT_MESSAGE_TEMPLATE = "{jakarta.validation.constraints.Future.message}";

    private TimeRangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder violationBuilder;

    @Mock
    private NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() throws Exception {
        validator = new TimeRangeValidator();

        setPrivateField(validator, "startField", "startsAt");
        setPrivateField(validator, "endField", "endsAt");

        lenient().when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
        lenient().when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        lenient().when(nodeBuilder.addConstraintViolation()).thenReturn(context);
        lenient().when(context.getDefaultConstraintMessageTemplate()).thenReturn(DEFAULT_MESSAGE_TEMPLATE);
    }

    private static void setPrivateField(final Object target, final String fieldName, final Object value)
            throws Exception {
        final Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Setter
    @Getter
    static class TimeRangeDto {
        private OffsetDateTime startsAt;
        private OffsetDateTime endsAt;
    }

    @Test
    void nullValueIsValid() {
        final boolean result = validator.isValid(null, context);

        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void bothNullStartAndEndAreValid() {
        final TimeRangeDto dto = new TimeRangeDto(); // startsAt = null, endsAt = null

        final boolean result = validator.isValid(dto, context);

        assertTrue(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void startInPastIsInvalid() {
        final TimeRangeDto dto = new TimeRangeDto();
        dto.setStartsAt(OffsetDateTime.now().minusMinutes(10)); // past
        dto.setEndsAt(null);

        final boolean result = validator.isValid(dto, context);

        assertFalse(result);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(DEFAULT_MESSAGE_TEMPLATE);
        verify(violationBuilder).addPropertyNode("startsAt");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void endInPastIsInvalidAndOrderViolationAddsSecondError() {
        final TimeRangeDto dto = new TimeRangeDto();
        final OffsetDateTime now = OffsetDateTime.now();
        dto.setStartsAt(now.plusHours(2)); // future
        dto.setEndsAt(now.minusHours(1)); // past

        final boolean result = validator.isValid(dto, context);

        assertFalse(result);

        verify(context).disableDefaultConstraintViolation();
        verify(context, times(2)).buildConstraintViolationWithTemplate(DEFAULT_MESSAGE_TEMPLATE);
        verify(violationBuilder, times(2)).addPropertyNode("endsAt");
        verify(nodeBuilder, times(2)).addConstraintViolation();
    }

    @Test
    void startAfterOrEqualEndInFutureIsInvalidByOrder() {
        final TimeRangeDto dto = new TimeRangeDto();
        final OffsetDateTime now = OffsetDateTime.now();
        dto.setStartsAt(now.plusHours(3));
        dto.setEndsAt(now.plusHours(1));

        final boolean result = validator.isValid(dto, context);

        assertFalse(result);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(DEFAULT_MESSAGE_TEMPLATE);
        verify(violationBuilder).addPropertyNode("endsAt");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void validFutureRangeIsValid() {
        final TimeRangeDto dto = new TimeRangeDto();
        final OffsetDateTime now = OffsetDateTime.now();
        dto.setStartsAt(now.plusHours(1));
        dto.setEndsAt(now.plusHours(2));

        final boolean result = validator.isValid(dto, context);

        assertTrue(result);

        verify(context).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void nonOffsetDateTimeTypeIsIgnoredAndConsideredValid() throws Exception {
        setPrivateField(validator, "startField", "someOtherField");
        setPrivateField(validator, "endField", "anotherField");

        final TimeRangeDto dto = new TimeRangeDto();

        final boolean result = validator.isValid(dto, context);

        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }
}
