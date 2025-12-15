// pinia
import { defineStore } from 'pinia';

// types
import type { PurchaseOrderState as State } from '@/types/stores/purchaseOrder';

// api
import { fetchPurchaseOrder } from '@/services/api/purchaseOrder';

// stores
import useBillingStore from '@/stores/billing';
import useShippingStore from '@/stores/shipping';
import useConsentStore from '@/stores/consent';

export default defineStore('purchaseOrder', {
  state(): State {
    return {
      purchaseOrder: null,
    };
  },

  getters: {
    customer(state: State) {
      return state.purchaseOrder?.customer;
    },

    startsAt(state: State): Date | null {
      const startsAt = state.purchaseOrder?.startsAt;
      return startsAt ? new Date(startsAt) : null;
    },

    endsAt(state: State): Date | null {
      const endsAt = state.purchaseOrder?.endsAt;
      return endsAt ? new Date(endsAt) : null;
    },
  },

  actions: {
    async load(id: string) {
      const billingStore = useBillingStore();
      const shippingStore = useShippingStore();
      const consentStore = useConsentStore();

      const purchaseOrder = await fetchPurchaseOrder(id);

      this.purchaseOrder = purchaseOrder;
      billingStore.init(purchaseOrder);
      shippingStore.init(purchaseOrder);
      consentStore.init(purchaseOrder);
    },

    isLoaded(id: string) {
      return !!this.purchaseOrder?.id && this.purchaseOrder?.id === id;
    },

    async clear() {
      this.$reset();
    },
  },
});
