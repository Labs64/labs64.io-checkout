package io.labs64.checkout.web.tenant;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import io.labs64.authcontext.core.AuthContext;
import io.labs64.authcontext.core.AuthContextHolder;
import io.labs64.authcontext.core.AuthHeaders;
import io.labs64.checkout.exception.TenantRequiredException;
import io.labs64.checkout.messages.Messages;
import jakarta.annotation.Nullable;
import lombok.Setter;

/**
 * Supplies the tenant for the current request from the trusted gateway
 * auth-context ({@code X-Auth-Tenant}). The explicit setter remains
 * for tests and non-web callers; when unset, the bound
 * {@link io.labs64.authcontext.core.AuthContext} is consulted, then the dev-only
 * {@code labs64.tenant.default} fallback (for gateway-less local runs).
 */
@Component
@RequestScope
@ConditionalOnWebApplication
public class RequestTenantProvider implements TenantProvider {
    private static final Logger log = LoggerFactory.getLogger(RequestTenantProvider.class);

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
        return AuthContextHolder.get()
                .map(AuthContext::tenantId)
                .map(StringUtils::trimToNull)
                .orElse(StringUtils.trimToNull(defaultTenantId));
    }

    public String requireTenantId() {
        final String id = getTenantId();

        if (StringUtils.isBlank(id)) {
            log.warn("Tenant required but not provided — missing X-Auth-Tenant header or labs64.tenant.default config");
            throw new TenantRequiredException(msg.get("error.tenant.required", AuthHeaders.TENANT));
        }

        return id;
    }
}
