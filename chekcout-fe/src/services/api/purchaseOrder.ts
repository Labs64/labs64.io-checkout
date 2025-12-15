import baseAxios from '@/services/api/baseAxios';

// types
import type { PurchaseOrder } from '@/types/services/api/backend';

// composable
import useEnv from '@/composables/useEnv';

const { getEnv } = useEnv();

export async function fetchPurchaseOrder(id: string): Promise<PurchaseOrder> {
  const endpoint = `${getEnv('VITE_API_URL')}/purchase-orders/${id}`;
  const { data: purchaseOrder } = await baseAxios.get<PurchaseOrder>(endpoint);
  return purchaseOrder;
}

// export async function checkoutPurchaseOrder(id: string, billing, shipping, paymentMethod): Promise<PurchaseOrder> {
//   const endpoint = `${getEnv('VITE_API_URL')}/${id}`;
//   const { data: purchaseOrder } = await baseAxios.post<PurchaseOrder>(endpoint);
//   return purchaseOrder;
// }
