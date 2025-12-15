<template>
  <div class="card shadow-sm mb-2">
    <div class="card-header d-flex justify-content-between align-items-center">
      <div
        class="w-100 cursor-pointer"
        @click="store.selectedMethod = method"
      >
        <div class="form-check d-flex align-items-center gap-2">
          <VField
            v-model="store.selectedMethod"
            rules="required"
            type="radio"
            name="paymentMethod"
            class="form-check-input"
            :value="method"
          />

          <label class="form-check-label d-flex align-items-center gap-2">
            <i class="bi bi-stripe fs-5" />
            <span class="fw-semibold">{{ method?.name }}</span>
          </label>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

// stores
import usePaymentStore from '@/stores/payment';

const store = usePaymentStore();
const method = computed(() => store.methods.find((v) => v.id === 'STRIPE') ?? null);
</script>
