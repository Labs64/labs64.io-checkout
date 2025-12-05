package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerMessages {
    private final Messages msg;

    public String notFound(final UUID id) {
        return msg.get("error.not_found", "Customer", "ID", id);
    }

    public String emailDuplicate(final String email) {
        return msg.get("customer.email.duplicate", email);
    }
}
