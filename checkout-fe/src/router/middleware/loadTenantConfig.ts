// types
import type { Middleware } from '@/types/router/middleware';

// stores
import useTenantConfigStore from '@/stores/tenantConfig';
import usePurchaseOrderStore from '@/stores/purchaseOrder';

const middleware: Middleware = async () => {
  const { isLoaded, load } = useTenantConfigStore();
  const { purchaseOrder } = usePurchaseOrderStore();

  if (purchaseOrder?.tenantId && !isLoaded) {
    try {
      await load(purchaseOrder.tenantId);
    } catch (e) {
      return { name: '404' };
    }
  }
};

export default middleware;
