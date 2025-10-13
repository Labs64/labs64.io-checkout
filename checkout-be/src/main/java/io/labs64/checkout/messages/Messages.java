package io.labs64.checkout.messages;

import java.util.UUID;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Messages {
    private final MessageSourceAccessor m;

    public String get(final String code, final Object... args) {
        return m.getMessage(code, args);
    }
}