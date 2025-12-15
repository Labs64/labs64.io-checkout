// pinia
import { defineStore } from 'pinia';

// types
import type { ConsentState as State } from '@/types/stores/consent';
import type { PurchaseOrder } from '@/types/services/api/backend';

export default defineStore('consent', {
  state(): State {
    return {
      items: [],
      values: {},
    };
  },

  getters: {
    isValid(state): boolean {
      return !state.items.some((item) => item.required && !state.values[item.id]);
    },
  },

  actions: {
    init(po: PurchaseOrder) {
      this.values = {};
      this.items = po.consents || [];

      this.items.forEach((item) => {
        this.values[item.id] = false;
      });
    },

    async clear() {
      this.$reset();
    },
  },
});
