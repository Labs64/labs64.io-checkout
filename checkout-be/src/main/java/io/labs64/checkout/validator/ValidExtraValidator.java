package io.labs64.checkout.validator;

import java.util.Collection;
import java.util.Map;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidExtraValidator implements ConstraintValidator<ValidExtra, Object> {

    private int maxEntries;
    private int maxKeyLength;
    private String keyPattern;
    private boolean allowString, allowNumber, allowBoolean;
    private int maxStringValueLength;

    @Override
    public void initialize(final ValidExtra a) {
        this.maxEntries = a.maxEntries();
        this.maxKeyLength = a.maxKeyLength();
        this.keyPattern = a.keyPattern();
        this.allowString = a.allowString();
        this.allowNumber = a.allowNumber();
        this.allowBoolean = a.allowBoolean();
        this.maxStringValueLength = a.maxStringValueLength();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(final Object target, final ConstraintValidatorContext ctx) {
        if (target == null) {
            return true;
        }

        final Map<Object, Object> map;

        if (!(target instanceof final Map<?, ?> m)) {
            return violation(ctx, null, "{validation.map.must_be_map}");
        }

        map = (Map<Object, Object>) m;

        // check size
        if (map.size() > maxEntries) {
            return violation(ctx, null, "validation.extra.too_many_entries", Map.of("max", maxEntries));
        }

        boolean valid = true;

        for (final Map.Entry<Object, Object> e : map.entrySet()) {
            final Object rawKey = e.getKey();
            final Object val = e.getValue();

            // key must be a string and not empty
            if (!(rawKey instanceof final String key)) {
                valid &= violation(ctx, rawKey, "{validation.extra.key.must_be_string}");
                continue;
            }

            if (key.isBlank()) {
                valid &= violation(ctx, key, "{validation.extra.key.blank}");
            }

            if (key.length() > maxKeyLength) {
                valid &= violation(ctx, key, "validation.extra.key.length", Map.of("max", maxKeyLength));
            }

            if (!keyPattern.isBlank() && !key.matches(keyPattern)) {
                valid &= violation(ctx, key, "{validation.extra.key.pattern}");
            }

            // value null - is not allowed
            if (val == null) {
                valid &= violation(ctx, key, "{validation.extra.value.not_null}");
                continue;
            }

            final boolean isString = allowString && val instanceof String;
            final boolean isNumber = allowNumber && val instanceof Number;
            final boolean isBoolean = allowBoolean && val instanceof Boolean;

            boolean isValueValid = isString || isNumber || isBoolean;

            if (isValueValid && val instanceof final String s && s.length() > maxStringValueLength) {
                valid &= violation(ctx, key, "{validation.extra.value.length}", Map.of("max", maxStringValueLength));
                continue;
            }

            // nested structures is not allowed
            if (val instanceof Map || val instanceof Collection || val.getClass().isArray()) {
                isValueValid = false;
            }

            // NaN/Infinity
            if (val instanceof final Double d && (d.isNaN() || d.isInfinite())) {
                isValueValid = false;
            }

            if (val instanceof final Float f && (f.isNaN() || f.isInfinite())) {
                isValueValid = false;
            }

            if (!isValueValid) {
                valid &= violation(ctx, key, "{validation.extra.value.type}");
            }
        }

        return valid;
    }

    private boolean violation(final ConstraintValidatorContext ctx, final Object key, final String msg) {
        return violation(ctx, key, msg, null);
    }

    private boolean violation(final ConstraintValidatorContext ctx, final Object key, final String msg,
            final Map<String, Object> params) {
        ctx.disableDefaultConstraintViolation();

        final var hv = ctx.unwrap(HibernateConstraintValidatorContext.class);

        if (key != null) {
            hv.addMessageParameter("key", key);
        }

        if (params != null) {
            params.forEach(hv::addMessageParameter);
        }

        hv.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        return false;
    }
}