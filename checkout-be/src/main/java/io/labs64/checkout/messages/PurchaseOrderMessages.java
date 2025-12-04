package io.labs64.checkout.messages;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseOrderMessages {
    private final Messages msg;

    public String notFound(final UUID id) {
        return msg.get("error.not_found", "PurchaseOrder", "ID", id);
    }

    public String checkoutNotStarted(final OffsetDateTime startsAt) {
        return msg.get("purchase_order.checkout.not_started", startsAt);
    }

    public String checkoutExpired(final OffsetDateTime endsAt) {
        return msg.get("purchase_order.checkout.expired", endsAt);
    }
}
