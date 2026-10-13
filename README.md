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

<p align="center"><img src="docs/assets/checkout-hero.svg" alt="Checkout: your products become a purchase order; Checkout validates currency, tax, time window and consents, records the buyer's checkout transaction and serves a brandable checkout page." width="880"></p>

[**Get started**](#get-started) · [Features](#features) · [Architecture](#architecture) · [AI agents](#using-checkout-with-ai-agents) · [Ecosystem](#the-labs64io-ecosystem) · [Documentation](https://labs64.io/docs/modules/checkout/) · [labs64.io](https://labs64.io)

> [!NOTE]
> Checkout currently records the buyer's checkout transaction as `PENDING` and returns `nextAction: NONE`. Handing the transaction to the Payment Gateway is not connected yet.

## Why Checkout?

A checkout is more than a payment form: it has to hold the order, the buyer, the tax and currency rules and the consents, and it has to look like your brand. Checkout gives you that as an API and a page:

1. **One order model.** A purchase order carries items, currency, tax, an optional customer and a validity window.
2. **Rules enforced for you.** Currency, tax rate, time range, extras and required consents are validated before a transaction exists.
3. **A checkout page that is yours.** A Vue 3 UI with runtime configuration and i18n, served from a static image.

## Get started

Four ways in. Pick the one that matches what you have; each is complete on its own.

```
 1  install.sh             2  Kubernetes             3  Docker                 4  Source
┌───────────────────────┐ ┌───────────────────────┐ ┌───────────────────────┐ ┌───────────────────────┐
│ The whole platform    │ │ Checkout on your      │ │ The Checkout API on   │ │ Build the API and     │
│ with gateway + auth;  │ │ cluster with your     │ │ your machine, from    │ │ the page, change      │
│ enable Checkout in    │ │ own PostgreSQL        │ │ the published image   │ │ them, contribute      │
│ the BYO profile       │ │ and gateway           │ │ and your policies     │ │ back                  │
├───────────────────────┤ ├───────────────────────┤ ├───────────────────────┤ ├───────────────────────┤
│ needs  a cluster,     │ │ needs  a cluster,     │ │ needs  Docker         │ │ needs  JDK 25, Maven, │
│        kubectl, helm  │ │        kubectl, helm  │ │                       │ │        Node, Docker   │
│ time   ~10 minutes    │ │ time   an afternoon   │ │ time   ~4 minutes     │ │ time   first build    │
└───────────────────────┘ └───────────────────────┘ └───────────────────────┘ └───────────────────────┘
 evaluate everything       run it for real           see an order happen       work on the code
```

Not sure? Start with **3** to create an order and check out through the API, or **1** to see Checkout inside the platform.

### 1. `install.sh`: the quick ecosystem start

One script installs the platform (gateway, authentication, AuditFlow and Payment Gateway) on any Kubernetes cluster, then proves it with a real request. Checkout is an optional module: answer **yes** to "Enable Checkout" in the BYO profile. No cluster? A throwaway one on your laptop is enough:

```bash
k3d cluster create labs64io-demo -p "8000:80@loadbalancer" --k3s-arg "--disable=traefik@server:0"   # optional

curl -fsSL https://raw.githubusercontent.com/Labs64/labs64.io-helm-charts/master/install.sh -o install.sh
bash install.sh
```

Press Enter three times to take the defaults. You finish with a gateway you can `curl`, interactive API docs and a working token flow:

```text
  PASS  Obtained a demo access token from mock-oidc
  PASS  Published a demo audit event (HTTP 200) — first good request succeeded

 Labs64.IO is ready.
   Base URL:  http://localhost:8000
   API docs:  http://localhost:8000/swagger-ui/
```

The Quickstart profile installs AuditFlow and Payment Gateway only; Checkout is offered by the BYO profile, which asks for your identity provider's discovery URL. The same script later runs `status`, `stop`, `start` and `uninstall`. This is an evaluation install with a demo identity provider: keep it on a local or private cluster. Details: [Quickstart](https://labs64.io/docs/getting-started/quickstart/) · [`install.sh`](https://github.com/Labs64/labs64.io-helm-charts/blob/master/install.sh).

### 2. Kubernetes: on your own cluster

Install Checkout from its Helm chart (API and, with `ui.enabled`, the checkout page):

```bash
helm repo add labs64io https://labs64.github.io/labs64.io-helm-charts
helm repo update
helm install my-checkout labs64io/checkout -n labs64io --create-namespace
```

Checkout needs PostgreSQL and, behind the gateway, the central policy engine. The [`preflight`](https://labs64.io/docs/getting-started/deploy-to-kubernetes/) chart checks your database from inside the cluster first. Guides: [Deploy to your Kubernetes cluster](https://labs64.io/docs/getting-started/deploy-to-kubernetes/) · [Helm chart on Artifact Hub](https://artifacthub.io/packages/helm/labs64io-helm-charts/checkout).

### 3. Docker: on your machine

Only Docker and Git are needed. Authorization policies are generated from the API contract by the build, so step 1 produces them once inside a Maven container; step 2 runs the **published** image next to PostgreSQL and the policy engine.

**Start the stack** (about two minutes for the first step, once):

```bash
git clone https://github.com/Labs64/labs64.io-checkout.git && cd labs64.io-checkout

docker run --rm -v "$PWD":/src -w /src/checkout-be maven:3.9-eclipse-temurin-25 \
  mvn -B -q -DskipTests package                      # generates the authorization policies

docker compose -f docker-compose.images.yml up -d    # PostgreSQL, Cerbos and the Checkout API
```

**Create an order and check out.** The headers stand in for the ones the API gateway adds after authenticating a caller:

```bash
api() { curl -s -H "X-Auth-User: demo-user" -H "X-Auth-Tenant: demo" -H "X-Request-ID: demo" \
  -H "X-Auth-Scopes: purchase-order:read,purchase-order:write,purchase-order:checkout,checkout-transaction:read" \
  -H "Content-Type: application/json" "$@"; }

# 1. A purchase order: one item, 30.00 USD
ORDER=$(api -X POST localhost:8080/purchase-orders -d '{
  "currency": "USD",
  "items": [{"name": "Widget Pro", "sku": "WIDGET-PRO", "quantity": 1, "price": 3000}]
}' | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

# 2. The buyer checks out
api -X POST localhost:8080/purchase-orders/$ORDER/checkout -d '{
  "paymentMethod": "STRIPE", "consents": {}, "extra": {},
  "billingInfo": {"firstName": "Jane", "lastName": "Doe", "email": "jane.doe@example.com",
                  "country": "US", "city": "San Francisco", "address1": "123 Market St", "postalCode": "94105"}
}' | grep -o '"status":"[A-Z]*"\|"nextAction":{"type":"[A-Z]*"'
```

```
"status":"PENDING"
"nextAction":{"type":"NONE"
```

The order is validated and a checkout transaction exists in `PENDING`. Clean up with `docker compose -f docker-compose.images.yml down -v`. Swagger UI is at <http://localhost:8080/swagger-ui.html>.

This is an evaluation setup without an API gateway: do not expose port 8080. The checkout **page** needs the gateway in front of the API, so it comes with option 1 or the [full ecosystem](https://labs64.io/docs/getting-started/run-the-full-ecosystem-locally/).

### 4. Source: build and change it

Needs Docker with Compose v2, JDK 25 and Maven 3.6.3+ (and Node for the page):

```bash
cd checkout-be
mvn -B clean package -DskipTests                       # build the API and generate the policies
docker compose -f docker-compose.dev.yml up -d         # PostgreSQL, Cerbos and the API from your jar

cd ../checkout-fe
npm install && npm run dev                              # the checkout page on http://localhost:5173
```

[`AGENTS.md`](AGENTS.md) describes the layout, commands and tests; the [Contributing guide](https://labs64.io/docs/development/contributing/) covers the workflow.

### Where next?

Everything below is in the [Checkout documentation](https://labs64.io/docs/modules/checkout/):

| I want to… | Read |
|---|---|
| Understand the order and checkout model | [Checkout documentation](https://labs64.io/docs/modules/checkout/) |
| Call the API from my storefront | [REST APIs](https://labs64.io/docs/modules/checkout/#rest-apis) |
| Brand the checkout page | [Configuration](https://labs64.io/docs/modules/checkout/#configuration) |
| Compare all the ways to run Labs64.IO | [Choose a deployment path](https://labs64.io/docs/getting-started/choose-a-deployment-path/) |

## Features

- **Purchase orders**: items, currency, tax, validity window and an optional customer, over a REST API.
- **Validation**: currency, tax rate, time range, extras and required consents are checked before checkout.
- **Consent-aware**: orders can require named consents; checkout is refused until they are accepted.
- **Multi-tenant**: tenant and scopes come from the trusted gateway headers; every query is tenant-scoped.
- **Whitelabel page**: Vue 3 UI with runtime `env.json` configuration, so one image runs in every environment.
- **i18n-ready**: translated UI via `vue-i18n`.
- **OpenAPI-first**: the contract is the source of truth; server interfaces and authorization policies are generated from it.

## Architecture

```
  ┌──────────────────────────────┐        ┌──────────────────────────────────┐
  │ Buyer's browser              │        │ Your storefront · backend        │
  └──────────────┬───────────────┘        └───────────────┬──────────────────┘
                 │ /checkout/{orderId}                    │ POST /purchase-orders
  ┌──────────────▼───────────────┐                        │ POST …/{id}/checkout
  │ checkout-fe                  │                        │
  │ Vue 3 · Vite · nginx         │                        │
  └──────────────┬───────────────┘                        │
                 │ REST (via the API gateway)             │
  ┌──────────────▼────────────────────────────────────────▼──────────┐
  │ checkout-be            Java 25 · Spring Boot 4                   │
  │ orders · customers · consents · checkout transactions            │
  └───────────────┬───────────────────────────────┬──────────────────┘
                  │                               │ is_allowed
          ┌───────▼────────┐              ┌───────▼────────┐
          │ PostgreSQL     │              │ Cerbos         │
          │ (Flyway)       │              │ (policy engine)│
          └────────────────┘              └────────────────┘
```

Details are in the [Checkout documentation](https://labs64.io/docs/modules/checkout/).

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

Contributions of all sizes are welcome. Fork, branch, run the tests described in [`AGENTS.md`](AGENTS.md), and open a pull request. If you are unsure whether something fits, open an issue first.

## License

The core of the *Labs64.IO Ecosystem* is entirely open source and free forever. Community modules are licensed under [Apache License 2.0](LICENSE).

---

<p align="center">
  <a href="https://labs64.io"><img alt="Labs64.IO" src="https://labs64.io/docs/assets/images/labs64-logo.png" width="110"></a><br>
  <sub>Made by the <a href="https://labs64.io">Labs64</a> team and community.</sub>
</p>
