package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShippingInfoMessages {
    private final Messages msg;

    public String notFound(final UUID id) {
        return msg.get("error.not_found", "ShippingInfo", id);
    }

    public String deleteConflict(final UUID id) {
        return msg.get("shipping_info.delete.conflict", id);
    }

    public String consentRequired() {
        return msg.get("shipping_info.consent.required");
    }
}
