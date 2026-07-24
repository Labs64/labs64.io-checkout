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
