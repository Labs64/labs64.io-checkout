package io.labs64.checkout.web.authcontext;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Fail-closed filter that reads gateway auth headers (RFC-03) into
 * {@link UserContextHolder} and rejects requests without a tenant header on
 * non-public paths.
 *
 * <p>Runs immediately after {@link io.labs64.checkout.web.filter.CorrelationIdFilter}
 * so that the correlation ID is already in the MDC when a 401 is written.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuthContextFilter extends OncePerRequestFilter {

    private final AuthContextProperties properties;

    public AuthContextFilter(final AuthContextProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String tenantId = StringUtils.trimToNull(request.getHeader(AuthHeaders.TENANT));
            final String userId = StringUtils.trimToNull(request.getHeader(AuthHeaders.USER));
            final String roles = StringUtils.trimToNull(request.getHeader(AuthHeaders.ROLES));

            if (tenantId != null) {
                UserContextHolder.set(new UserContext(tenantId, userId, roles));
            } else if (!isPublicPath(request.getRequestURI())) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(),
                        "Missing required gateway header: " + AuthHeaders.TENANT);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private boolean isPublicPath(final String uri) {
        return properties.getPublicPaths().stream().anyMatch(uri::startsWith);
    }
}
