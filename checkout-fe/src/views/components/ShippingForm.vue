<template>
  <div class="card shadow-sm-sm">
    <div class="card-header d-flex justify-content-between align-items-center">
      <div class="d-flex align-items-center gap-3">
        <i class="bi bi-truck fs-4 m-2" />
        <div>
          <div class="fw-semibold">{{ fullName }}</div>
          <div class="text-body-secondary small">
            {{ fullAddress }}
          </div>
          <div class="text-body-secondary small">
            {{ form.phone }}
          </div>
        </div>
      </div>

      <FormToggleButton
        :opened="isOpened"
        @toggle="toggle"
      />
    </div>

    <div
      v-show="isOpened"
      class="card-body"
    >
      <VForm
        ref="formRef"
        v-slot="{ errors }"
      >
        <div class="d-flex justify-content-end align-items-center">
          <BButton
            variant="link"
            size="sm"
            class="text-decoration-none mb-1"
            @click="copyFromBilling"
          >
            {{ t('shipping.copyFromBilling') }}
          </BButton>
        </div>

        <!-- FIRST & LAST NAME -->
        <div class="row g-3 mb-2">
          <div class="col-md">
            <div class="form-floating">
              <VField
                v-model="form.firstName"
                name="firstName"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.firstName }"
                :label="t('common.form.firstName')"
                :placeholder="t('common.form.firstName')"
              />

              <label>{{ t('common.form.firstName') }}</label>

              <VErrorMessage
                as="div"
                name="firstName"
                class="invalid-feedback"
              />
            </div>
          </div>

          <div class="col-md">
            <div class="form-floating">
              <VField
                v-model="form.lastName"
                rules="required"
                name="lastName"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.lastName }"
                :label="t('common.form.lastName')"
                :placeholder="t('common.form.lastName')"
              />

              <label>{{ t('common.form.lastName') }}<span class="text-danger">&nbsp;*</span></label>

              <VErrorMessage
                as="div"
                name="lastName"
                class="invalid-feedback"
              />
            </div>
          </div>
        </div>

        <!-- PHONE -->
        <div class="row mb-2">
          <div class="col">
            <div class="form-floating">
              <VField
                v-model="form.phone"
                name="phone"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.phone }"
                :label="t('common.form.phone')"
                :placeholder="t('common.form.phone')"
              />

              <label>{{ t('common.form.phone') }}</label>

              <VErrorMessage
                as="div"
                name="phone"
                class="invalid-feedback"
              />
            </div>
          </div>
        </div>

        <!-- COUNTRY -->
        <div class="row mb-2">
          <div class="col-md">
            <div class="form-floating">
              <VField
                v-model="form.country"
                rules="required"
                as="select"
                name="country"
                class="form-select"
                :class="{ 'is-invalid': !!errors.country }"
                :label="t('common.form.country')"
              >
                <option value="US">United States</option>
                <option value="GE">Germany</option>
                <option value="UA">Ukraine</option>
              </VField>

              <label>{{ t('common.form.country') }}<span class="text-danger">&nbsp;*</span></label>

              <VErrorMessage
                as="div"
                name="country"
                class="invalid-feedback"
              />
            </div>
          </div>
        </div>

        <!-- CITY & STATE & POSTAL CODE -->
        <div class="row g-3 mb-2">
          <!-- CITY -->
          <div class="col-md">
            <div class="form-floating">
              <VField
                v-model="form.city"
                rules="required"
                name="city"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.name }"
                :label="t('common.form.city')"
                :placeholder="t('common.form.city')"
              />

              <label>{{ t('common.form.city') }}<span class="text-danger">&nbsp;*</span></label>

              <VErrorMessage
                as="div"
                name="city"
                class="invalid-feedback"
              />
            </div>
          </div>

          <!-- STATE -->
          <div class="col-md">
            <div class="form-floating">
              <VField
                v-model="form.state"
                as="select"
                name="state"
                class="form-select"
                :class="{ 'is-invalid': !!errors.state }"
                :label="t('common.form.state')"
              >
                <option value="CT">Connecticut</option>
                <option value="MS">Mississippi</option>
                <option value="HI">Hawaii</option>
              </VField>

              <label>{{ t('common.form.state') }}</label>

              <VErrorMessage
                as="div"
                name="state"
                class="invalid-feedback"
              />
            </div>
          </div>

          <!-- POSTAL CODE -->
          <div class="col-md">
            <div class="form-floating">
              <VField
                v-model="form.postalCode"
                rules="required"
                name="postalCode"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.state }"
                :label="t('common.form.postalCode')"
                :placeholder="t('common.form.postalCode')"
              />

              <label>{{ t('common.form.postalCode') }}<span class="text-danger">&nbsp;*</span></label>

              <VErrorMessage
                as="div"
                name="postalCode"
                class="invalid-feedback"
              />
            </div>
          </div>
        </div>

        <!-- ADDRESS 1 -->
        <div class="row mb-2">
          <div class="col">
            <div class="form-floating">
              <VField
                v-model="form.address1"
                rules="required"
                name="address1"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.address1 }"
                :label="t('common.form.address1')"
                :placeholder="t('common.form.address1')"
              />

              <label>{{ t('common.form.address1') }}<span class="text-danger">&nbsp;*</span></label>

              <VErrorMessage
                as="div"
                name="address1"
                class="invalid-feedback"
              />
            </div>
          </div>
        </div>

        <!-- ADDRESS 2 -->
        <div class="row mb-2">
          <div class="col">
            <div class="form-floating">
              <VField
                v-model="form.address2"
                name="address2"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': !!errors.address2 }"
                :label="t('common.form.address2')"
                :placeholder="t('common.form.address2')"
              />

              <label>{{ t('common.form.address2') }}</label>

              <VErrorMessage
                as="div"
                name="address2"
                class="invalid-feedback"
              />
            </div>
          </div>
        </div>
      </VForm>
    </div>
    <div
      v-show="isOpened"
      class="card-footer"
    >
      <BFormCheckbox v-model="billingStore.$persistRemember">
        <span class="small text-secondary">{{ t('shipping.saveDetails') }}</span>
      </BFormCheckbox>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

// i18n
import { useI18n } from 'vue-i18n';

// stores
import useShippingStore from '@/stores/shipping';
import useBillingStore from '@/stores/billing';

// types
import type { Form } from 'vee-validate';

// lodash
import { merge } from 'lodash-es';
import FormToggleButton from '@/views/components/FormToggleButton.vue';

const { t } = useI18n();

const shippingStore = useShippingStore();
const billingStore = useBillingStore();

let { form } = shippingStore;
const isOpened = ref(!(form.lastName && form.country && form.city && form.postalCode && form.address1));
const fullName = computed(() => `${form.firstName} ${form.lastName}`);
const fullAddress = computed(() =>
  [form.address1, form.city, form.state, form.postalCode, form.country].filter((v) => v).join(', '),
);

const formRef = ref<typeof Form | null>(null);

async function open() {
  isOpened.value = true;
}

async function close() {
  const { valid } = await validate();

  if (valid) {
    isOpened.value = false;
  }
}

async function toggle() {
  if (isOpened.value) {
    await close();
  } else {
    await open();
  }
}

async function validate(): Promise<{ valid: boolean; errors: string[] }> {
  return formRef.value ? formRef.value.validate() : { valid: true, errors: [] };
}

function copyFromBilling() {
  form = merge(form, billingStore.form);
}
</script>
