package io.labs64.checkout.security;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import io.labs64.authcontext.test.AuthEnforcementContract;
import io.labs64.authcontext.test.ModulePepHarness;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/**
 * Item 2 (roadmap): every operation protected by effective OpenAPI
 * {@code security} or {@code x-labs64.auth} in the canonical spec is called
 * without credentials and must be refused.
 *
 * <p>Checkout declares no public operations at all, so the whole API surface is
 * expected here — which makes the completeness guard the important assertion:
 * an empty suite would mean the spec stopped declaring protection, not that
 * everything is fine.
 *
 * <p>Controllers are discovered and the filter is built from the real
 * {@code application.yml} — see {@link ModulePepHarness}. A 404 fails: an
 * unmapped route means the call never reached an enforcement point.
 *
 * <p>Scope: module-layer PEP. The gateway edge is proven separately by the
 * generated suite in {@code labs64.io-tests}; both layers must hold.
 */
class AuthEnforcementContractTest {

    /** The canonical contract, not a build artifact. */
    private static final Path SPEC = Path.of("src", "main", "resources", "openapi", "openapi-checkout-v1.yaml");

    private final MockMvc mockMvc = ModulePepHarness.withProductionAuthFilter("io.labs64.checkout.controller");

    @TestFactory
    Stream<DynamicTest> everyProtectedOperationRefusesAnonymousCallers() throws IOException {
        return AuthEnforcementContract.rejectsAnonymousAccess(SPEC, (method, path) ->
                mockMvc.perform(request(HttpMethod.valueOf(method), path))
                        .andReturn().getResponse().getStatus());
    }
}
