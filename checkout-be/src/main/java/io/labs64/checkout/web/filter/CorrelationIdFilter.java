package io.labs64.checkout.web.filter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

/**
 * Filter that propagates the {@code X-Correlation-ID} header across requests and responses.
 *
 * <p>If the incoming request contains an {@code X-Correlation-ID} header, it is reused.
 * Otherwise, a new UUID is generated. The correlation ID is added to the SLF4J MDC
 * so it appears in all log entries, and it is echoed back in the response header.</p>
 *
 * <p>Consistent with the ecosystem convention used across checkout, payment-gateway,
 * and auditflow modules.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    /** Paths that bypass correlation-ID processing (health probes, favicon). */
    private static final Set<String> SKIP_PATHS = Set.of(
            "/actuator/health",
            "/actuator/health/liveness",
            "/actuator/health/readiness",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        if (SKIP_PATHS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        // Validate the incoming correlation ID: accept only safe UUID-like values
        // (alphanumeric, hyphens, underscores, max 64 chars) to prevent log injection
        // and header injection.
        if (correlationId == null || correlationId.isBlank() || !isValidCorrelationId(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    /**
     * Validates that a correlation ID contains only safe characters.
     * Accepts UUID format and similar alphanumeric identifiers up to 64 characters.
     * Rejects anything that could be used for log injection (newlines, CRLF) or header injection.
     */
    private boolean isValidCorrelationId(final String correlationId) {
        if (correlationId.length() > 64) {
            return false;
        }
        return correlationId.matches("[a-zA-Z0-9\\-_]+");
    }
}

