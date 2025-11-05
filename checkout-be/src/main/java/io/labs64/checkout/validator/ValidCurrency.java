package io.labs64.checkout.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCurrencyValidator.class)
public @interface ValidCurrency {
    String message() default "{validation.currency.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
