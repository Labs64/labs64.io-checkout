import type { MiddlewareConfig } from '@/types/router/middleware';

const config: MiddlewareConfig = {
  /**
   * The application's global HTTP middleware stack.
   * These middleware are run during every request to your application.
   */
  global: {
    // executed before any route
    before: [],

    // executed after any route
    after: [],
  },

  /**
   * The application's route middleware.
   * These middleware may be assigned to groups or used individually.
   */
  route: {},
};

export default config;
