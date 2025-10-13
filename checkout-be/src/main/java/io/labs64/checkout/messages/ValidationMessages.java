package io.labs64.checkout.messages;

import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidationMessages {
    private final Messages msg;

    public String invalidCurrency(final Set<String> allowedCurrencies) {
        return msg.get("validation.currency.invalid", String.join(",", allowedCurrencies));
    }

    public String invalidField(final String field, final String message) {
        return msg.get("validation.field.invalid", field, message);
    }

    public String failed() {
        return msg.get("validation.failed");
    }
}
