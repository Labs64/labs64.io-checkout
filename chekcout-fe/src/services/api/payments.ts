import type { PaymentMethod } from '@/types/services/api/backend.ts';

export async function fetchPaymentMethods(): Promise<PaymentMethod[]> {
  return [
    {
      id: 'PAYPAL',
      name: 'PayPal',
    },
    {
      id: 'STRIPE',
      name: 'Stripe',
    },
  ];
}
