package io.labs64.checkout.web.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantConstants {
    public static final String ATTR_TENANT_ID = "tenantId";
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";
}
