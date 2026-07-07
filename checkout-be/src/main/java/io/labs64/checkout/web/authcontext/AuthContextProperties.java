package io.labs64.checkout.web.authcontext;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the local auth-context enforcement (RFC-03).
 *
 * <pre>
 * labs64:
 *   auth-context:
 *     enabled: true
 *     public-paths:
 *       - /actuator
 *       - /v3/api-docs
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "labs64.auth-context")
public class AuthContextProperties {

    /**
     * Enable fail-closed gateway-auth enforcement. Defaults to {@code true}.
     */
    private boolean enabled = true;

    /**
     * Path prefixes that are accessible without gateway auth headers.
     */
    private List<String> publicPaths = List.of();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(final List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }
}
