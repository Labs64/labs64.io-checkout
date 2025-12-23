// types
import type { Middleware } from '@/types/router/middleware';

// lodash
import { castArray } from 'lodash-es';

// stores
import usePurchaseOrderStore from '@/stores/purchaseOrder';

// utils
import { toNotFoundRoute } from '@/router/utils';

const middleware: Middleware = async (to) => {
  const store = usePurchaseOrderStore();

  const [id] = castArray(to.params.id);

  const notFound = toNotFoundRoute(to);

  if (!id) {
    return notFound;
  }

  if (!store.isLoaded(id)) {
    try {
      await store.load(id);
    } catch (e) {
      return notFound;
    }
  }
};

export default middleware;
