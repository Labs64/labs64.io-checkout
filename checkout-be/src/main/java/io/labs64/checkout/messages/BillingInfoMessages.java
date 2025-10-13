package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillingInfoMessages {
    private final Messages msg;

    public String notFoundById(final UUID id) {
        return msg.get("error.not_found", "BillingInfo", id);
    }

    public String deleteConflictLinkedToCheckoutIntents(final UUID id) {
        return msg.get("billing_info.delete.conflict", id);
    }

    public String consentRequired() {
        return msg.get("billing_info.consent.required");
    }

    public String lockedByTerminalCheckoutIntent(final UUID id) {
        return msg.get("billing_info.locked.by.terminal.checkout_intent", id);
    }
}
