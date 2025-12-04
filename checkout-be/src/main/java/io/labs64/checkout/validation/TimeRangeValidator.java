package io.labs64.checkout.validation;

import java.time.OffsetDateTime;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(final ValidTimeRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        final BeanWrapper beanWrapper = new BeanWrapperImpl(value);

        final Object startObj;
        final Object endObj;

        try {
            startObj = beanWrapper.getPropertyValue(startField);
            endObj = beanWrapper.getPropertyValue(endField);
        } catch (final Exception ex) {
            return true;
        }

        if (startObj != null && !(startObj instanceof OffsetDateTime)) {
            return true;
        }
        if (endObj != null && !(endObj instanceof OffsetDateTime)) {
            return true;
        }

        final OffsetDateTime startsAt = (OffsetDateTime) startObj;
        final OffsetDateTime endsAt = (OffsetDateTime) endObj;

        final OffsetDateTime now = OffsetDateTime.now();

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (startsAt != null && !startsAt.isAfter(now)) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(startField).addConstraintViolation();
            valid = false;
        }

        if (endsAt != null && !endsAt.isAfter(now)) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(endField).addConstraintViolation();
            valid = false;
        }

        if (startsAt != null && endsAt != null && !startsAt.isBefore(endsAt)) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(endField).addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
