import type { Routes } from '@/types/router/routes';

const routes: Routes = [
  {
    path: '/checkout/:id',
    name: 'checkout',
    component: () => import('@/views/pages/CheckoutPage.vue'),
    meta: { middleware: ['ensureCheckoutContext'] },
  },

  // 404 page
  {
    path: '/:pathMatch(.*)*',
    name: '404Page',
    component: () => import('@/views/pages/404Page.vue'),
  },
];

export default routes;
