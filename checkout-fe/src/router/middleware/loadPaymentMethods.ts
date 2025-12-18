// types
import type { Middleware } from '@/types/router/middleware';

// stores
import usePaymentStore from '@/stores/payment';

const middleware: Middleware = async () => {
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
