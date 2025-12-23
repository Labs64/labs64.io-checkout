import { useNProgress } from '@vueuse/integrations/useNProgress';
import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import routes from '@/router/routes';
import useRouteMiddleware from '@/router/middleware';

let history = createWebHistory(import.meta.env.BASE_URL);

if (import.meta.env.VITE_ROUTER_HASH_MODE === 'true') {
  history = createWebHashHistory(import.meta.env.BASE_URL);
}

const router = createRouter({ history, routes });

const { navigationGuard } = useRouteMiddleware();

router.beforeEach(async (to, from) => {
  const { isLoading } = useNProgress(null, { showSpinner: false });

  isLoading.value = true;
  return navigationGuard(to, from);
});

router.afterEach(() => {
  const { isLoading } = useNProgress();
  isLoading.value = false;
});

export default router;
