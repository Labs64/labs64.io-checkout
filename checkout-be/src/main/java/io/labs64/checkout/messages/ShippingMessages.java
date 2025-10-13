package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShippingMessages {
    private final Messages msg;

    public String notFound(final UUID id) {
        return msg.get("error.not_found", "Shipping", id);
    }

    public String deleteConflict(final UUID id) {
        return msg.get("shipping.delete.conflict", id);
    }
}
