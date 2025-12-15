import type { Routes } from '@/types/router/routes';
import CheckoutPage from '@/views/pages/CheckoutPage.vue';

const routes: Routes = [
  {
    path: '/checkout/:id',
    name: 'checkout',
    component: CheckoutPage,
    meta: { middleware: ['loadPaymentMethods'] },
  },
];

export default routes;
