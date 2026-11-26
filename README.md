# Labs64.IO :: Checkout

**A whitelabel checkout that looks like you: from order to transaction.** Checkout is the order and checkout layer in front of your payments: it holds a purchase order with its items, currency, tax, an optional customer and a validity window, validates currency, tax rate, time range, extras and required consents, and records the checkout transaction, through a REST API and a themable Vue 3 checkout page. It does not yet hand the transaction to the Payment Gateway: it records it as `PENDING`.

[![CI (Backend)](https://img.shields.io/github/actions/workflow/status/Labs64/labs64.io-checkout/labs64io-be-ci.yml?label=CI%20(Backend)&logo=github)](https://github.com/Labs64/labs64.io-checkout/actions/workflows/labs64io-be-ci.yml)
[![CI (Frontend)](https://img.shields.io/github/actions/workflow/status/Labs64/labs64.io-checkout/labs64io-fe-ci.yml?label=CI%20(Frontend)&logo=github)](https://github.com/Labs64/labs64.io-checkout/actions/workflows/labs64io-fe-ci.yml)
[![Docker Image Version](https://img.shields.io/docker/v/labs64/checkout?label=Docker&logo=docker&logoColor=white&color=1C90ED)](https://hub.docker.com/r/labs64/checkout)
[![Docker Image Version (UI)](https://img.shields.io/docker/v/labs64/checkout-ui?label=Docker%20(UI)&logo=docker&logoColor=white&color=1C90ED)](https://hub.docker.com/r/labs64/checkout-ui)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/labs64io-helm-charts)](https://artifacthub.io/packages/helm/labs64io-helm-charts/checkout)
[![📖 Documentation](https://img.shields.io/badge/📖-Documentation-AB6543.svg)](https://labs64.io/docs/modules/checkout/)
[![Labs64.IO Ecosystem](https://img.shields.io/badge/Labs64.IO-Ecosystem-E14817.svg?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9Ii01IC01IDE1NSAxNTUiPjxwYXRoIGZpbGw9IiNmZmYiIGQ9Ik0zIDNoMTM5djEzOUgzWk02OSAzdjEzOWg3VjNaTTQzIDQzdjU5aDI2VjQzWk03NiA0M3Y1OWgyNlY0M1oiLz48cGF0aCBmaWxsPSIjYmJiIiBkPSJNNDkgNDloNDd2NDdINDlaIi8%2BPC9zdmc%2B)](https://labs64.io)

<p align="center"><img src="assets/checkout-hero.svg" alt="Checkout: One checkout page re-skins itself for three brands, each with its own colour, domain, language and currency, while the order, tax validation, consent record and PENDING transaction stay the same." width="880"></p>

## The Labs64.IO Ecosystem

Checkout is one module of the [Labs64.IO Ecosystem](https://labs64.io). Its documentation is at [labs64.io/docs/modules/checkout](https://labs64.io/docs/modules/checkout/); [all modules are listed in the documentation](https://labs64.io/docs/services/).

---

<p align="center">
  <a href="https://labs64.io"><img alt="Labs64.IO" src="https://labs64.io/docs/assets/images/labs64-logo.png" width="110"></a><br>
  <sub>Made by the <a href="https://labs64.io">Labs64</a> team and community.</sub>
</p>
