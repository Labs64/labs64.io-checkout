import type { Routes } from '@/types/router/routes';
import CheckoutPage from '@/views/pages/CheckoutPage.vue';

const routes: Routes = [
  {
    path: '/',
    name: 'home',
    component: CheckoutPage,
  },
];

export default routes;
