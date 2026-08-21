# AGENTS.md — Labs64.IO :: Checkout

Whitelabel checkout page: Vue 3 frontend + Spring Boot backend.

## Ecosystem role

- `checkout-be` publishes order events to RabbitMQ → consumed by auditflow-be.
- `checkout-fe` served behind Traefik, authenticated via traefik-authproxy (OIDC/JWT).
- Swagger UI at `gateway.localhost/checkout/v3/api-docs`.

## Repository layout

| Path | Service | Stack | Port |
|------|---------|-------|------|
| `checkout-be/` | Backend | Java 25, Spring Boot 4.1.0, Maven | 8080 |
| `checkout-fe/` | Frontend | Vue 3, Vite 7, Pinia, TypeScript | 5173 (dev) |

## Critical guardrails

1. **OpenAPI-first**: canonical spec at `checkout-be/src/main/resources/openapi/openapi-checkout-v1.yaml`.
2. **Never edit generated Java** under `target/`.
3. **Never hardcode credentials** — env vars or K8s Secrets only.
4. **Preserve `l64user`** (uid/gid 1064) in Dockerfiles.

## Backend details

- **Package**: `io.labs64.checkout`
- **Key services**: `PurchaseOrderService`, `CustomerService`, `CheckoutTransactionService`
- **Multi-tenancy**: tenant derives from the trusted gateway auth-context (`X-Auth-Tenant`, `auth-context-spring-boot-starter`); `RequestTenantProvider` supplies it (dev fallback: `labs64.tenant.default`).
- **Validation**: custom validators for currency, tax rate, time range, extras.
- **Database**: PostgreSQL. **Broker**: RabbitMQ → auditflow-be.

## Frontend details

- Vue 3 Composition API, Bootstrap 5 + Bootstrap Vue Next.
- i18n: `vue-i18n` + `@vee-validate/i18n`. Validation: `vee-validate`.
- E2E: Playwright. Unit: Vitest.
- Runtime config: `env.json` mounted as ConfigMap in K8s.

## Build, run, test

```bash
# Backend
cd checkout-be
just build          # mvn clean package -DskipTests
just test           # mvn clean verify
just dev-up         # build + docker compose up
just dev-watch      # build + docker compose watch (auto-restart)
just dev-down       # stop containers

# Frontend
cd checkout-fe
just build          # npm install + npm run build
just dev-up         # docker compose up
just dev-down       # docker compose down
```

Local URLs: Swagger `:8080/swagger-ui.html`, frontend `:5173`.

## Where to make common changes

| Goal | Where |
|------|-------|
| API contract | `checkout-be/src/main/resources/openapi/openapi-checkout-v1.yaml` |
| Backend service | `checkout-be/src/main/java/io/labs64/checkout/service/` |
| REST controller | `checkout-be/src/main/java/io/labs64/checkout/controller/` |
| Custom validator | `checkout-be/src/main/java/io/labs64/checkout/validation/` |
| Frontend components | `checkout-fe/src/` |
