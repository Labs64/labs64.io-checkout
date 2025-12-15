<template>
  <component
    :is="resolveComponent(method.id)"
    v-for="method in supportedMethods"
    :key="method.id"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';

// stores
import usePaymentStore from '@/stores/payment';

// components
import PayPalMethod from '@/views/components/PaymentMethods/PayPalMethod.vue';
import StripeMethod from '@/views/components/PaymentMethods/StripeMethod.vue';

const methodComponents = {
  PAYPAL: PayPalMethod,
  STRIPE: StripeMethod,
} as const;

const paymentStore = usePaymentStore();

const { methods } = paymentStore;

const supportedMethods = computed(() =>
  methods.filter((method) => methodComponents[method.id as keyof typeof methodComponents]),
);

const resolveComponent = (id: string) => methodComponents[id as keyof typeof methodComponents] ?? null;
</script>
