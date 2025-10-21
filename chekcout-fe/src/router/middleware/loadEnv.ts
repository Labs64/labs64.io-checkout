import useEnv from '@/composables/useEnv';
import type { Middleware } from '@/types/router/middleware';

/**
 * Middleware for fetching dynamic env.
 *
 * This middleware loads env.
 *
 * @param to - The target route the user is navigating to.
 * @param from - The route the user is coming from.
 */
const middleware: Middleware = async (to, from) => {
  const { isLoaded, loadEnv } = useEnv();

  try {
    if (!isLoaded) {
      await loadEnv();
    }
  } catch (error) {
    console.error('Failed to load env.json:', error);
    return { name: '500' };
  }
};

export default middleware;
