<p align="center"><img src="https://raw.githubusercontent.com/Labs64/.github/refs/heads/master/assets/labs64-io-ecosystem.png"></p>

# Labs64.IO :: Checkout

[![Docker Image Version](https://img.shields.io/docker/v/labs64/checkout?logo=docker&logoColor=%23E14817&color=%23E14817)](https://hub.docker.com/r/labs64/checkout)
[![Docker Image Version (UI)](https://img.shields.io/docker/v/labs64/checkout-ui?logo=docker&logoColor=%23E14817&color=%23E14817)](https://hub.docker.com/r/labs64/checkout-ui)
[![Helm Chart](https://img.shields.io/badge/Helm%20Chart-checkout-0F1689?logo=helm)](https://artifacthub.io/packages/helm/labs64io-helm-charts/checkout)
[![📖 Documentation](https://img.shields.io/badge/📖-Documentation-AB6543.svg)](https://github.com/Labs64/labs64.io-docs)

## Whitelabel Checkout Page

Embeddable, brandable checkout for the Labs64.IO ecosystem. It turns a purchase
order into a completed transaction — collecting customer details, applying tax and
currency rules, and driving the payment through the [Payment Gateway](https://github.com/Labs64/labs64.io-payment-gateway).
Order events are published to RabbitMQ and consumed by [AuditFlow](https://github.com/Labs64/labs64.io-auditflow)
for compliance logging.

The repository ships two services:

| Path | Stack | Role |
|------|-------|------|
| `checkout-be/` | Java 25, Spring Boot 4.x | REST API: purchase orders, customers, checkout transactions |
| `checkout-fe/` | Vue 3, Vite, Pinia, TypeScript | Whitelabel checkout UI |

## Key Features

- Multi-tenant: tenant is derived from the trusted gateway auth-context (`X-Auth-Tenant`).
- Currency, tax-rate, time-range and extras validation.
- i18n-ready UI (`vue-i18n`) with runtime config injected via a mounted `env.json` ConfigMap.
- OpenAPI-first backend — the contract at `checkout-be/src/main/resources/openapi/openapi-checkout.yaml` is the source of truth.

For the full developer guide (build/run/test, where to make changes), see [`AGENTS.md`](AGENTS.md).

## Frontend quick start

```sh
cd checkout-fe
npm install
npm run dev          # hot-reload dev server (http://localhost:5173)
npm run build        # type-check + production build
npm run test:unit    # Vitest unit tests
npm run test:e2e     # Playwright end-to-end tests
npm run lint         # ESLint
```

See the [Vite Configuration Reference](https://vite.dev/config/) to customize the build.

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Labs64/labs64.io-checkout&type=Date)](https://www.star-history.com/#Labs64/labs64.io-checkout&Date)

## License

The core of the *Labs64.IO Ecosystem* is entirely open source and free forever. Community modules are licensed under [Apache License 2.0](LICENSE).
