# Labs64.IO :: Checkout — justfile

default:
    @just --list

# ─────────────────────────────────────────────────────────────────────────────
# Testing
# ─────────────────────────────────────────────────────────────────────────────

# Run E2E tests for this module via labs64.io-tests
test-e2e:
    @just -f ../labs64.io-tests/justfile test-module checkout

# Alias to run E2E tests
test: test-e2e

# Generate JSON Schema contracts from OpenAPI into a labs64.io checkout
schemas-generate output_root:
    test -d "{{output_root}}" || (echo "Output root does not exist: {{output_root}}" >&2; exit 2)
    mvn -ntp --file checkout-be/pom.xml --activate-profiles contract-schemas exec:java@generate-contract-schemas -Dcontract-schema.output-root="{{output_root}}"

# Validate and preview JSON Schema generation without writing files
schemas-dry-run output_root:
    test -d "{{output_root}}" || (echo "Output root does not exist: {{output_root}}" >&2; exit 2)
    mvn -B -ntp --file checkout-be/pom.xml --activate-profiles contract-schemas exec:java@dry-run-contract-schemas -Dcontract-schema.output-root="{{output_root}}"
