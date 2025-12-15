// types
import type { Middleware } from '@/types/router/middleware';

// lodash
import { castArray } from 'lodash-es';

// stores
import usePaymentStore from '@/stores/payment';

const middleware: Middleware = async (to) => {
  const store = usePaymentStore();

  if (!store.isLoaded) {
    try {
      await store.load();
    } catch (e) {
      return { name: '404' };
    }
  }
};

export default middleware;
