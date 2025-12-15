import 'pinia';
import type { PersistOptions } from '@/types/stores/plugins/PersistPlugin.ts';

declare module 'pinia' {
  export interface DefineStoreOptionsBase<S, Store> {
    persist?: PersistOptions<S>;
  }

  export interface PiniaCustomProperties<Id, S, G, A> {
    $options: {
      id: Id;
      state?: () => S;
      getters?: G;
      actions?: A;
      persist?: PersistOptions<S>;
    };

    // persist plugin (storing remember:* fields)
    $persistRemember: boolean;
  }
}
