# Labs64.IO :: Checkout

**A whitelabel checkout that looks like you: from order to transaction.**
*Part of the [Labs64.IO Ecosystem](https://labs64.io): the order and checkout layer in front of your payments.*

[![CI (Backend)](https://github.com/Labs64/labs64.io-checkout/actions/workflows/labs64io-be-ci.yml/badge.svg)](https://github.com/Labs64/labs64.io-checkout/actions/workflows/labs64io-be-ci.yml)
[![CI (Frontend)](https://github.com/Labs64/labs64.io-checkout/actions/workflows/labs64io-fe-ci.yml/badge.svg)](https://github.com/Labs64/labs64.io-checkout/actions/workflows/labs64io-fe-ci.yml)
[![Docker Image Version](https://img.shields.io/docker/v/labs64/checkout?label=Docker&logo=docker&logoColor=white&color=1C90ED)](https://hub.docker.com/r/labs64/checkout)
[![Docker Image Version (UI)](https://img.shields.io/docker/v/labs64/checkout-ui?label=Docker%20(UI)&logo=docker&logoColor=white&color=1C90ED)](https://hub.docker.com/r/labs64/checkout-ui)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/labs64io-helm-charts)](https://artifacthub.io/packages/helm/labs64io-helm-charts/checkout)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![📖 Documentation](https://img.shields.io/badge/📖-Documentation-AB6543.svg)](https://labs64.io/docs/modules/checkout/)

<p align="center"><img src="docs/assets/checkout-hero.svg" alt="Checkout: One checkout page re-skins itself for three brands, each with its own colour, domain, language and currency, while the order, tax validation, consent record and PENDING transaction stay the same." width="880"></p>

[**Get started**](https://labs64.io/docs/modules/checkout/#quick-start) · [Features](#features) · [Architecture](https://labs64.io/docs/modules/checkout/#architecture) · [AI agents](#using-checkout-with-ai-agents) · [Ecosystem](#the-labs64io-ecosystem) · [Documentation](https://labs64.io/docs/modules/checkout/) · [labs64.io](https://labs64.io)

> [!NOTE]
> Checkout currently records the buyer's checkout transaction as `PENDING` and returns `nextAction: NONE`. Handing the transaction to the Payment Gateway is not connected yet.

## Why Checkout?

A checkout is more than a payment form. It has to hold the order, the buyer, the tax and currency rules and the consents, and it has to look like your brand rather than someone else's. Checkout gives you that as an API plus a page you can theme.

1. **One order model.** A purchase order carries items, currency, tax, an optional customer and a validity window.
2. **Rules enforced for you.** Currency, tax rate, time range, extras and required consents are validated before a transaction exists.
3. **A checkout page that is yours.** A Vue 3 page with i18n and runtime configuration, served from a small static image.

**A good fit if**

- You sell digital or physical goods and want an order and checkout service you can brand and host yourself.
- Consents (terms, marketing, data processing) must be captured per order and enforced before checkout.
- You are building on the Labs64.IO platform and want orders, payments and audit to fit together.

**Probably not what you need if**

- You need payments executed end to end today: Checkout currently records the transaction and does not yet hand it to the Payment Gateway.
- You want a complete shop (catalogue, cart, inventory): Checkout starts at the purchase order.

## Features

- **Purchase orders**: items, currency, tax, validity window and an optional customer, over a REST API.
- **Validation**: currency, tax rate, time range, extras and required consents are checked before checkout.
- **Consent-aware**: orders can require named consents; checkout is refused until they are accepted.
- **Multi-tenant**: tenant and scopes come from the trusted gateway headers; every query is tenant-scoped.
- **Whitelabel page**: Vue 3 UI with runtime `env.json` configuration, so one image runs in every environment.
- **i18n-ready**: translated UI via `vue-i18n`.
- **OpenAPI-first**: the contract is the source of truth; server interfaces and authorization policies are generated from it.

## What works today, and what does not yet

We would rather you hear it from us than discover it in a demo.

| Works today | Not yet |
|---|---|
| Purchase orders, customers and checkout transactions over REST | Handing the transaction to the Payment Gateway, so no `nextAction` redirect yet |
| Validation of currency, tax, time window, extras and consents | Publishing events to AuditFlow |
| Tenant isolation and scope-based authorization through the gateway | The checkout page driving the final checkout call (it loads and renders an order) |
| A themable Vue 3 page with i18n and runtime configuration | |

If your use case needs the missing pieces, the [issue tracker](https://github.com/Labs64/labs64.io-checkout/issues) is where to say so: that is how priorities get set.

## Using Checkout with AI agents

Checkout turns a basket into a validated, auditable order that an agent can create and finalize with plain REST.

- **Machine-readable contract.** The OpenAPI v1 spec lives in [`openapi-checkout-v1.yaml`](checkout-be/src/main/resources/openapi/openapi-checkout-v1.yaml) and is served by the running API; generate a client or tool definition from it.
- **Least privilege.** Operations require scopes such as `purchase-order:write` and `purchase-order:checkout`; give an agent only what its task needs.
- **Send complete bodies.** Checkout expects `consents` and `extra` to be present (use `{}`); omitting them currently returns `500` rather than a validation error.
- **Working on the code with an agent?** [`AGENTS.md`](AGENTS.md) and [`CLAUDE.md`](CLAUDE.md) describe the layout, conventions and test commands.

## The Labs64.IO Ecosystem

Checkout is one module of the [Labs64.IO Ecosystem](https://labs64.io); [every module is documented here](https://labs64.io/docs/).

| Module | What it does | Link |
|---|---|---|
| **AuditFlow** | Audit-event routing pipeline | [labs64.io-auditflow](https://github.com/Labs64/labs64.io-auditflow) |
| **API Gateway** | Authenticating, policy-enforcing edge for all modules | [labs64.io-authproxy](https://github.com/Labs64/labs64.io-authproxy) |
| **Payment Gateway** | One API over multiple payment providers | [labs64.io-payment-gateway](https://github.com/Labs64/labs64.io-payment-gateway) |
| **Checkout** | Embeddable, brandable checkout | [labs64.io-checkout](https://github.com/Labs64/labs64.io-checkout) |
| **Customer Portal** | Customer self-service UI | [labs64.io-customer-portal](https://github.com/Labs64/labs64.io-customer-portal) |
| **Commons** | Shared Java and Python libraries | [labs64.io-commons](https://github.com/Labs64/labs64.io-commons) |
| **Helm Charts** | Kubernetes deployment for every module | [labs64.io-helm-charts](https://github.com/Labs64/labs64.io-helm-charts) |
| **Workspace** | Dev container and multi-repo tooling | [labs64.io-workspace](https://github.com/Labs64/labs64.io-workspace) |
| **Tests** | Cross-module regression suite | [labs64.io-tests](https://github.com/Labs64/labs64.io-tests) |
| **Docs** | Published documentation | [labs64.io-docs](https://github.com/Labs64/labs64.io-docs) |
| **NetLicensing** | Licensing-as-a-Service: license and entitlement management | [netlicensing.io](https://netlicensing.io) |
| **GuideChimp** | Interactive guided product tours | [labs64.com/guidechimp](https://www.labs64.com/guidechimp/) |

## Contributing

We would love your help. Connecting Checkout to the Payment Gateway, publishing events to AuditFlow and finishing the checkout page are the biggest open areas. Fork the repository, make your change on a branch, run the checks described in [`AGENTS.md`](AGENTS.md) and open a pull request. Not sure whether an idea fits? Open an issue first and we will talk it through; that is a perfectly good way to start.

## License

Everything in this repository is open source under the [Apache License 2.0](LICENSE): you can use it, change it and ship it commercially. The core of the *Labs64.IO Ecosystem* is free forever; community modules are Apache-2.0 licensed.

---

<p align="center">
  <a href="https://labs64.io"><img alt="Labs64.IO" src="https://labs64.io/docs/assets/images/labs64-logo.png" width="110"></a><br>
  <sub>Made by the <a href="https://labs64.io">Labs64</a> team and community.</sub>
</p>
