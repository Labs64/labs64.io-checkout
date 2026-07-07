package io.labs64.checkout.web.authcontext;

/**
 * Canonical header names injected by the trusted gateway (RFC-03).
 */
public final class AuthHeaders {

    public static final String TENANT = "X-Auth-Tenant";
    public static final String USER = "X-Auth-User";
    public static final String ROLES = "X-Auth-Roles";

    private AuthHeaders() {
    }
}
