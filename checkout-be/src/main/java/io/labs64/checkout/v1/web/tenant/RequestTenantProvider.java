package io.labs64.checkout.v1.web.tenant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import io.labs64.checkout.exception.TenantRequiredException;
import io.labs64.checkout.messages.Messages;
import io.labs64.checkout.v1.web.constants.TenantConstants;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;

@Component
@RequestScope
@ConditionalOnWebApplication
public class RequestTenantProvider implements TenantProvider {
    private final Messages msg;

    @Setter
    @Nullable
    private String tenantId;

    public RequestTenantProvider(final Messages msg) {
        this.msg = msg;
    }

    @Override
    @Nullable
    public String getTenantId() {
        return tenantId;
    }

    public String requireTenantId() {
        final String id = getTenantId();

        if (StringUtils.isBlank(id)) {
            throw new TenantRequiredException(msg.get("error.tenant.required", TenantConstants.HEADER_TENANT_ID));
        }

        return id;
    }
}