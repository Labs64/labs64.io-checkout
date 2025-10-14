package io.labs64.checkout.v1.web.filter;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.labs64.checkout.v1.web.constants.TenantConstants;
import io.labs64.checkout.v1.web.tenant.RequestTenantProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantHeaderFilter extends OncePerRequestFilter {
    private final ObjectProvider<RequestTenantProvider> tenantProvider;

    public TenantHeaderFilter(final ObjectProvider<RequestTenantProvider> tenantProvider) {
        this.tenantProvider = tenantProvider;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws ServletException, IOException {

        String tenantId = request.getHeader(TenantConstants.HEADER_TENANT_ID);

        if (StringUtils.isNotBlank(tenantId)) {
            tenantId = tenantId.trim();

            tenantProvider.getObject().setTenantId(tenantId);
            request.setAttribute(TenantConstants.ATTR_TENANT_ID, tenantId);
        }

        chain.doFilter(request, response);
    }
}