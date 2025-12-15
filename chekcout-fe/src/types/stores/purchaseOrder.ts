import type { PurchaseOrder } from '@/types/services/api/backend';

export interface PurchaseOrderState {
  purchaseOrder: PurchaseOrder | null;
}
