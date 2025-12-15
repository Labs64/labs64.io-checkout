<template>
  <div class="bg-light min-vh-100">
    <TheHeader
      :brand-name="config?.brandName"
      :logo="config?.logo"
    />

    <div class="container-lg py-4">
      <div class="row g-4">
        <!-- LEFT COLUMN -->
        <div class="col-12 col-lg-8">
          <!-- ORDER -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">{{ t('order.title') }}</h2>

            <OrderItems class="mb-3" />
          </section>

          <!-- BILLING INFO -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">{{ t('billing.title') }}</h2>

            <BillingForm />
          </section>

          <!-- SHIPPING -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">{{ t('shipping.title') }}</h2>

            <ShippingForm />
          </section>

          <!-- PAYMENT -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">{{ t('payment.title') }}</h2>

            <PaymentMethods />
          </section>
        </div>

        <!-- RIGHT COLUMN (SUMMARY) -->
        <aside class="col-12 col-lg-4">
          <!-- Promo -->
          <div
            v-show="false"
            class="card shadow-sm-sm mb-3"
          >
            <div class="card-body">
              <div class="d-flex mb-1">
                <VField
                  placeholder="Promo Code"
                  name="promoCode"
                  class="form-control me-2 flex-grow-1"
                />

                <BButton variant="outline-secondary">Apply</BButton>
              </div>
            </div>
          </div>

          <!-- Summary -->
          <OrderSummary />
        </aside>
      </div>
    </div>

    <TheFooter />
  </div>
</template>

<script setup async lang="ts">
// i18n
import { useI18n } from 'vue-i18n';

// components
import TheHeader from '@/views/components/TheHeader.vue';
import TheFooter from '@/views/components/TheFooter.vue';
import BillingForm from '@/views/components/BillingForm.vue';
import ShippingForm from '@/views/components/ShippingForm.vue';
import OrderItems from '@/views/components/OrderItems.vue';
import PaymentMethods from '@/views/components/PaymentMethods/MethodsList.vue';
import OrderSummary from '@/views/components/OrderSummary.vue';

// stores
import useTenantConfigStore from '@/stores/tenantConfig';

const { t } = useI18n();

const tenantConfigStore = useTenantConfigStore();
const { config } = tenantConfigStore;
</script>
