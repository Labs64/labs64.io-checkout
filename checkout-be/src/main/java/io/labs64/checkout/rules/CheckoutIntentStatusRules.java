package io.labs64.checkout.rules;

import java.util.EnumSet;
import java.util.Set;

import io.labs64.checkout.model.CheckoutIntentStatus;

public class CheckoutIntentStatusRules {
    private static final EnumSet<CheckoutIntentStatus> FINISHED_STATUSES = EnumSet.of(CheckoutIntentStatus.COMPLETED,
            CheckoutIntentStatus.CANCELED, CheckoutIntentStatus.FAILED);

    public static boolean isFinished(final CheckoutIntentStatus status) {
        return FINISHED_STATUSES.contains(status);
    }

    public static Set<CheckoutIntentStatus> finishedStatuses() {
        return Set.copyOf(FINISHED_STATUSES);
    }

}
