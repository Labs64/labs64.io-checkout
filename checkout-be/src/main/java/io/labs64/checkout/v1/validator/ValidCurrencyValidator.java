package io.labs64.checkout.v1.validator;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.labs64.checkout.config.CheckoutProperties;
import io.labs64.checkout.messages.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidCurrencyValidator implements ConstraintValidator<ValidCurrency, String> {
    private final ValidationMessages msg;
    final Set<String> allowed;

    public ValidCurrencyValidator(final ValidationMessages msg, final CheckoutProperties props) {
        this.msg = msg;

        allowed = props.getCurrency().stream().filter((c) -> c != null && !c.isEmpty())
                .map((c) -> c.trim().toUpperCase()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(final String target, final ConstraintValidatorContext ctx) {
        if (target == null || target.isEmpty()) {
            return true;
        }

        final String normalized = target.trim().toUpperCase();

        if (allowed.contains(normalized)) {
            return true;
        }

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg.invalidCurrency(allowed)).addConstraintViolation();

        return false;
    }
}