package io.labs64.checkout.web.authcontext;

/**
 * Immutable snapshot of the auth context populated from gateway headers (RFC-03).
 *
 * @param tenantId value of {@code X-Auth-Tenant}
 * @param userId   value of {@code X-Auth-User}
 * @param roles    value of {@code X-Auth-Roles}
 */
public record UserContext(String tenantId, String userId, String roles) {
}
