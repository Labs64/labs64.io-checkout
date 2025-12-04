package io.labs64.checkout.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeValidator.class)
public @interface ValidTimeRange {

    /**
     * Default message key from messages.properties.
     */
    String message() default "{validation.po.time_range.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Name of the start field (e.g. "startsAt").
     */
    String startField() default "startsAt";

    /**
     * Name of the end field (e.g. "endsAt").
     */
    String endField() default "endsAt";
}
