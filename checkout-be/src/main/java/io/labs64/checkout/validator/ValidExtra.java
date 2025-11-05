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
@Constraint(validatedBy = ValidExtraValidator.class)
public @interface ValidExtra {
    /** Maximum number of key-value pairs. */
    int maxEntries() default 50;

    /** Keys must be a string and not empty — additionally limits/pattern: */
    int maxKeyLength() default 40;

    /** e.g. "^[a-zA-Z0-9_.-]+$" or leave empty — no additional checking */
    String keyPattern() default "^[a-zA-Z0-9-_.@+&$:]+$";

    /** Value: allowed types (JSON scalars) */
    boolean allowString() default true;

    boolean allowNumber() default true; // Integer/Long/BigDecimal/Double/Float

    boolean allowBoolean() default true;

    /** Limit for string values */
    int maxStringValueLength() default 500;

    String message() default "{validation.extra.invalid_map}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
