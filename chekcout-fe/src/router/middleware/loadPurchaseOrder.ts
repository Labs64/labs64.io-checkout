// types
import type { Middleware } from '@/types/router/middleware';

// lodash
import { castArray } from 'lodash-es';

// stores
import usePurchaseOrderStore from '@/stores/purchaseOrder';

const middleware: Middleware = async (to) => {
  const store = usePurchaseOrderStore();

  const [id] = castArray(to.params.id);

  if (!id) {
    return { name: '404' };
  }

  if (!store.isLoaded(id)) {
    try {
      await store.load(id);
    } catch (e) {
      return { name: '404' };
    }
  }
};

export default middleware;
