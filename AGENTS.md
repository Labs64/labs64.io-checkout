# AGENTS.md — Labs64.IO :: Checkout

Guidance for AI agents working in this repository. Read this before making changes.

## What this project is

Whitelabel checkout page for the Labs64.IO ecosystem. Two independently deployable services: a Vue 3 frontend SPA and a Spring Boot backend REST API.

### Ecosystem role

- `checkout-be` publishes order events to RabbitMQ → consumed by `auditflow-be` for audit logging.
- `checkout-fe` is served behind Traefik, authenticated via `traefik-authproxy` (OIDC/JWT).
- Swagger UI at `gateway.localhost/checkout/v3/api-docs` aggregates the API docs.

## Repository layout

| Path | Service | Stack | Port | Role |
|------|---------|-------|------|------|
| `checkout-be/` | Backend | Java 25, Spring Boot 4.0.5, Maven | 8080 | REST API, order/customer/transaction management |
| `checkout-fe/` | Frontend | Vue 3, Vite 7, Pinia, TypeScript | 5173 (dev), 80 (nginx) | Whitelabel checkout UI |

## Critical guardrails

1. **Never edit OpenAPI-generated Java** under `target/`. Change the YAML spec and rebuild.
2. **Never hardcode credentials.** Use environment variables or Kubernetes Secrets.
3. **Preserve non-root user `l64user`** (uid/gid 1064) in all Dockerfiles.
4. **OpenAPI-first**: the canonical spec is at `checkout-be/src/main/resources/openapi/openapi-checkout.yaml`.
5. **Each repo has its own git history** — do not cross-commit between repositories.

## Backend (`checkout-be`) details

- **Build is OpenAPI-first.** Models and API interfaces are generated from `openapi-checkout.yaml` by `openapi-generator-maven-plugin`. Generated sources live under `target/generated-sources` and are git-ignored.
- **Package**: `io.labs64.checkout`
- **Key services**: `PurchaseOrderService`, `CustomerService`, `CheckoutTransactionService`
- **Key controllers**: `PurchaseOrderController`, `CustomerController`, `CheckoutTransactionController`
- **Multi-tenancy**: `TenantHeaderFilter` extracts tenant from request; `RequestTenantProvider` supplies it.
- **Validation**: custom validators for currency, tax rate, time range, extras (`ValidCurrency`, `ValidTaxRate`, `ValidTimeRange`, `ValidExtra`).
- **Cross-cutting**: `CorrelationIdFilter` (X-Correlation-ID), `GlobalExceptionHandler`, `CorsConfig`.
- **Observability**: Actuator + Micrometer Tracing (OTLP/HTTP) + Prometheus scrape at `/actuator/prometheus`.
- **Database**: PostgreSQL (orders, customers).
- **Message broker**: RabbitMQ — events published to auditflow-be.

### Dockerfile

- Base: `eclipse-temurin:25-alpine`
- Non-root user: `l64user` (uid/gid 1064)
- Entrypoint: `java $JAVA_OPTS -jar app.jar`

## Frontend (`checkout-fe`) details

- **Vue 3** with Composition API, TypeScript, Vite 7, Pinia state management.
- **UI**: Bootstrap 5 + Bootstrap Vue Next.
- **i18n**: `vue-i18n` with `@vee-validate/i18n`.
- **Validation**: `vee-validate` with `@vee-validate/rules`.
- **E2E tests**: Playwright (config: `playwright.config.ts`).
- **Unit tests**: Vitest.
- **Linting**: ESLint + oxlint + Prettier.
- **Runtime config**: `env.json` mounted as ConfigMap in Kubernetes.

### Dockerfile

- Multi-stage: `node:25` builder → `nginx:alpine` runtime
- Custom `nginx.conf` for SPA routing
- Exposes port 80

## Build, run, test

```bash
# Backend
cd checkout-be
just build              # mvn clean package -DskipTests
just test               # mvn clean verify
just dev-up             # build + docker compose up (PostgreSQL + RabbitMQ)
just dev-watch          # build + docker compose watch (auto-restart on changes)
just dev-down           # stop containers
just dev-clean          # stop + remove volumes

# Frontend
cd checkout-fe
just build              # npm install + npm run build
just dev-up             # docker compose up
just dev-down           # docker compose down
```

Local URLs: backend Swagger `http://localhost:8080/swagger-ui.html`, frontend `http://localhost:5173`.

## Conventions

- **Java 25** and **Maven 3.6.3+** enforced by `maven-enforcer-plugin`.
- **Spring Boot 4.0.5** with Spring Cloud 2025.x. Use reactive WebClient for HTTP calls.
- **Credentials from environment variables only** — never hardcode, never commit defaults.
- Backend tests: JUnit 5 + Spring Boot Test alongside source in `src/test/java/`.
- Frontend tests: Vitest (unit), Playwright (E2E).
- All Dockerfiles run as non-root user `l64user` (uid/gid 1064).
- Logging: backend uses SLF4J/Logback with logstash JSON encoder.

## Where to make common changes

| Goal | Where |
|------|-------|
| Change the API contract | `checkout-be/src/main/resources/openapi/openapi-checkout.yaml` |
| Add a backend service | `checkout-be/src/main/java/io/labs64/checkout/service/` |
| Add a REST controller | `checkout-be/src/main/java/io/labs64/checkout/controller/` |
| Add a custom validator | `checkout-be/src/main/java/io/labs64/checkout/validation/` |
| Modify frontend components | `checkout-fe/src/` |
| Change i18n translations | `checkout-fe/src/locales/` (or similar) |
| Modify nginx config | `checkout-fe/nginx.conf` |
