<template>
  <BCard
    v-if="purchaseOrder"
    class="position-sticky shadow-sm top-20"
  >
    <div class="d-flex justify-content-between text-body-secondary mb-1">
      <span>{{ t('order.summary.subtotal') }}</span>
      <span>{{ money(purchaseOrder.netAmount) }}</span>
    </div>

    <div class="d-flex justify-content-between text-body-secondary mb-1">
      <span>{{ t('order.summary.tax') }}</span>
      <span>+ {{ money(purchaseOrder.taxAmount) }}</span>
    </div>

    <div class="hr-line-dashed" />

    <div class="d-flex justify-content-between align-items-center mb-3">
      <span class="fw-semibold">
        {{ t('order.summary.total') }}
      </span>
      <span class="fw-semibold fs-5">
        {{ money(purchaseOrder.grossAmount) }}
      </span>
    </div>

    <template v-if="consents">
      <VForm
        ref="formRef"
        v-slot="{ errors }"
      >
        <div
          v-for="consent in consents"
          :key="consent.id"
          class="form-check mb-1"
        >
          <LocalScope
            v-slot="{ name }"
            :name="`consents.${consent.id}`"
          >
            <VField
              v-model="consentStore.values[consent.id]"
              :rules="{ required: consent.required }"
              :name="name"
              type="checkbox"
              class="form-check-input"
              :value="true"
              :unchecked-value="false"
            />
            <label
              class="form-check-label small"
              :class="{ 'text-danger': errors[name] }"
            >
              {{ consent.label }}

              <a
                v-if="consent.url"
                :href="consent.url"
                target="_blank"
              >
                <i
                  class="bi bi-box-arrow-up-right ms-1"
                  aria-hidden="true"
                />
              </a>
            </label>
          </LocalScope>
        </div>
      </VForm>
    </template>

    <div class="hr-line-solid my-3" />
    <button
      class="btn btn-primary w-100"
      @click="validate"
    >
      {{ t('common.buy') }}
    </button>
  </BCard>
</template>

<script setup async lang="ts">
import { ref } from 'vue';

// local scope
import { LocalScope } from '@allindevelopers/vue-local-scope';

// stores
import usePurchaseOrderStore from '@/stores/purchaseOrder';
import useConsentStore from '@/stores/consent';

// i18n
import { useI18n } from 'vue-i18n';

// composable
import { useMoney } from '@/composables/useMoney';

// types
import type { Form } from 'vee-validate';

const { t } = useI18n();

const purchaseOrderStore = usePurchaseOrderStore();
const { purchaseOrder } = purchaseOrderStore;

const { money } = useMoney(purchaseOrder?.currency);

const consentStore = useConsentStore();
const { items: consents } = consentStore;

const formRef = ref<typeof Form | null>(null);

async function validate(): Promise<{ valid: boolean; errors: string[] }> {
  return formRef.value ? formRef.value.validate() : { valid: true, errors: [] };
}
</script>

<style scoped>
.top-20 {
  top: 20px;
}
</style>
