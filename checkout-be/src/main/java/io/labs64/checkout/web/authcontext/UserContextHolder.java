package io.labs64.checkout.web.authcontext;

import java.util.Optional;

/**
 * Thread-local holder for the {@link UserContext} bound to the current request.
 *
 * <p>Set by {@link AuthContextFilter} at the start of each request and cleared
 * in the {@code finally} block to prevent context leaks across pooled threads.</p>
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    public static Optional<UserContext> get() {
        return Optional.ofNullable(HOLDER.get());
    }

    static void set(final UserContext ctx) {
        HOLDER.set(ctx);
    }

    static void clear() {
        HOLDER.remove();
    }

    private UserContextHolder() {
    }
}
