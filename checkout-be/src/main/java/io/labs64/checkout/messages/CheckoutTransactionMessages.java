package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CheckoutTransactionMessages {
    private final Messages msg;

    public String notFound(final UUID id) {
        return msg.get("error.not_found", "CheckoutTransaction", "ID", id);
    }
}
