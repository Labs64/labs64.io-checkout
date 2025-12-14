import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import routes from '@/router/routes';
import useRouteMiddleware from '@/router/middleware';

let history = createWebHistory(import.meta.env.BASE_URL);

if (import.meta.env.VITE_ROUTER_HASH_MODE === 'true') {
  history = createWebHashHistory(import.meta.env.BASE_URL);
}

const router = createRouter({ history, routes });

const { navigationGuard } = useRouteMiddleware();

router.beforeEach(async (to, from) => navigationGuard(to, from));

export default router;
