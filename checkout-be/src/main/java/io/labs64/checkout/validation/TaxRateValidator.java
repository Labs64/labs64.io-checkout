package io.labs64.checkout.validation;

import java.math.BigDecimal;

import io.labs64.checkout.model.Tax;
import io.labs64.checkout.model.TaxRateType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxRateValidator implements ConstraintValidator<ValidTaxRate, Tax> {

    @Override
    public void initialize(final ValidTaxRate constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Tax value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        final TaxRateType rateType = value.getRateType();
        final BigDecimal rate = value.getRate();

        if (rateType == null || rate == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("rate").addConstraintViolation();
            valid = false;
        }

        if (rateType == TaxRateType.FIXED && rate.scale() > 0) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("rate").addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
