import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import routes from './routes';
import useMiddleware from '@/router/middleware';

let history = createWebHistory(import.meta.env.BASE_URL);

if (import.meta.env.VITE_ROUTER_HASH_MODE === 'true') {
  history = createWebHashHistory(import.meta.env.BASE_URL);
}

const router = createRouter({ history, routes });

router.beforeEach((to, from, next) => {
  const { nextMiddleware } = useMiddleware();
  nextMiddleware(to, from, next);
});

export default router;
