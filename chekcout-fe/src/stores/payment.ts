// pinia
import { defineStore } from 'pinia';

// types
import type { PaymentState as State } from '@/types/stores/payment';
import { fetchPaymentMethods } from '@/services/api/payments';
import type { PaymentMethod } from '@/types/services/api/backend';

export default defineStore('payment', {
  state(): State {
    return {
      methods: [],
      selectedMethod: null,
      _loading: false,
      _loaded: false,
    };
  },

  getters: {
    isLoaded(state: State): boolean {
      return state._loaded;
    },

    isLoading(state: State): boolean {
      return state._loading;
    },
  },

  actions: {
    init(methods: PaymentMethod[]) {
      this.selectedMethod = null;
      this.methods = methods;
      this._loaded = true;

      const [method] = this.methods;

      if (method?.id) {
        this.selectedMethod = method;
      }
    },

    async load() {
      this._loading = true;
      this._loaded = false;

      try {
        const methods = await fetchPaymentMethods();
        this.init(methods);
      } finally {
        this._loading = false;
      }
    },

    clear() {
      this.$reset();
    },
  },
});
