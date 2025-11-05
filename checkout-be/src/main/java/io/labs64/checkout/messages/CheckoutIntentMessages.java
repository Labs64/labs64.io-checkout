package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.labs64.checkout.model.CheckoutIntentStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CheckoutIntentMessages {
    private final Messages msg;

    public String notFound(final UUID id) {
        return msg.get("error.not_found", "CheckoutIntent", id);
    }

    public String cannotUpdateFinished(final UUID id, final CheckoutIntentStatus status) {
        return msg.get("checkout_intent.update.finished", id, status);
    }
}
