package io.labs64.checkout.web.tenant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import io.labs64.authcontext.AuthHeaders;
import io.labs64.authcontext.UserContext;
import io.labs64.authcontext.UserContextHolder;
import io.labs64.checkout.exception.TenantRequiredException;
import io.labs64.checkout.messages.Messages;
import jakarta.annotation.Nullable;
import lombok.Setter;

/**
 * Supplies the tenant for the current request from the trusted gateway
 * auth-context ({@code X-Auth-Tenant}, RFC-03). The explicit setter remains
 * for tests and non-web callers; when unset, the bound
 * {@link io.labs64.authcontext.UserContext} is consulted, then the dev-only
 * {@code labs64.tenant.default} fallback (for gateway-less local runs).
 */
@Component
@RequestScope
@ConditionalOnWebApplication
public class RequestTenantProvider implements TenantProvider {
    private final Messages msg;
    private final String defaultTenantId;

    @Setter
    @Nullable
    private String tenantId;

    public RequestTenantProvider(final Messages msg,
            @Value("${labs64.tenant.default:}") final String defaultTenantId) {
        this.msg = msg;
        this.defaultTenantId = defaultTenantId;
    }

    @Override
    @Nullable
    public String getTenantId() {
        if (tenantId != null) {
            return StringUtils.trimToNull(tenantId);
        }
        return UserContextHolder.get()
                .map(UserContext::tenantId)
                .map(StringUtils::trimToNull)
                .orElse(StringUtils.trimToNull(defaultTenantId));
    }

    public String requireTenantId() {
        final String id = getTenantId();

        if (StringUtils.isBlank(id)) {
            throw new TenantRequiredException(msg.get("error.tenant.required", AuthHeaders.TENANT));
        }

        return id;
    }
}
