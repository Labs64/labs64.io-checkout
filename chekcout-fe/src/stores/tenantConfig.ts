// pinia
import { defineStore } from 'pinia';

// types
import type { TenantConfigState as State } from '@/types/stores/tenantConfig';

// api
import { fetchTenantConfig } from '@/services/api/tenantConfig';

export default defineStore('tenantConfig', {
  state(): State {
    return {
      tenantId: null,
      config: null,
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
    async load(tenantId: string) {
      this._loading = true;
      this._loaded = false;

      this.tenantId = tenantId;

      try {
        this.config = await fetchTenantConfig(tenantId);
        this._loaded = true;
      } finally {
        this._loading = false;
      }
    },

    async clear() {
      this.$reset();
    },
  },
});
