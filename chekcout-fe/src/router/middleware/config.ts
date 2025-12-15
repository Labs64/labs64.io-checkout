import type { MiddlewareConfig } from '@/types/router/middleware';

// middleware
import loadEnv from '@/router/middleware/loadEnv';
import loadPurchaseOrder from '@/router/middleware/loadPurchaseOrder';
import loadPaymentMethods from '@/router/middleware/loadPaymentMethods';
import loadTenantConfig from '@/router/middleware/loadTenantConfig';

const config: MiddlewareConfig = {
  /**
   * The application's global HTTP middleware stack.
   * These middleware are run during every request to your application.
   */
  global: {
    // executed before any route
    before: [loadEnv, loadPurchaseOrder, loadTenantConfig],

    // executed after any route
    after: [],
  },

  /**
   * The application's route middleware.
   * These middleware may be assigned to groups or used individually.
   */
  route: {
    loadPaymentMethods,
  },
};

export default config;
