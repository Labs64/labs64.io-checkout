*** Settings ***
Documentation    Checkout smoke checks — fast critical-path validation via the API edge.
Resource         ../../../labs64.io-tests/resources/checkout.resource
Suite Teardown   Delete All Sessions
# not-ga: the labs64/checkout and labs64/checkout-ui images have never been
# published, so charts/labs64io-ecosystem defaults checkout.enabled to false and
# no environment ever serves this module — see labs64.io-helm-charts/charts/
# labs64io-ecosystem/values.yaml. Drop this tag the same day that flips.
Test Tags        not-ga

*** Test Cases ***
Allow read-scoped access to customers (200)
    [Documentation]    GET /customers with customer:read token returns 200.
    [Tags]    checkout    smoke    critical
    Create Checkout Session With Scope    customer:read    checkout-read
    ${response}=    List Customers    checkout-read
    Response Status Should Be    ${response}    200
    Response Should Contain Key    ${response}    items

Allow read-scoped access to purchase orders (200)
    [Documentation]    GET /purchase-orders with purchase-order:read token returns 200.
    ...                Exercises the Cerbos Data PEP pilot (RFC-07): the domain policy's
    ...                PlanResources row filter must resolve and translate cleanly for an
    ...                ordinary tenant-scoped principal, not just return an authz decision.
    [Tags]    checkout    smoke    critical
    Create Checkout Session With Scope    purchase-order:read    checkout-po-read
    ${response}=    List Purchase Orders    checkout-po-read
    Response Status Should Be    ${response}    200
    Response Should Contain Key    ${response}    items
