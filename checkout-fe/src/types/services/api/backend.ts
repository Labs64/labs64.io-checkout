export type Extra = Record<string, string | number | boolean>;

export interface Tax {
  type: string;
  category: string;
  rateType: string;
  rate: number;
}

export interface Consent {
  id: string;
  label: string;
  url: string | null;
  required: boolean;
}

export interface BillingInfo {
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  phone: string | null;
  city: string | null;
  country: string | null;
  address1: string | null;
  address2: string | null;
  postalCode: string | null;
  state: string | null;
  vatId: string | null;
}

export interface ShippingInfo {
  firstName: string | null;
  lastName: string | null;
  phone: string | null;
  city: string | null;
  country: string | null;
  address1: string | null;
  address2: string | null;
  postalCode: string | null;
  state: string | null;
}

export interface Customer {
  id: string;
  firstName: string | null;
  lastName: string;
  email: string | null;
  phone: string | null;
  billingInfo: BillingInfo | null;
  shippingInfo: ShippingInfo | null;
  extra: Extra;
  createdAt: string;
  updatedAt: string;
}

export interface PurchaseOrderItem {
  name: string;
  description: string | null;
  url: string | null;
  image: string;
  sku: string | null;
  uom: string;
  quantity: number;
  price: number;
  tax: Tax | null;
  extra: Extra;
}

export interface PurchaseOrder {
  id: string;
  tenantId: string;
  customer: Customer | null;
  total: number;
  currency: string;
  items: PurchaseOrderItem[];
  consents: Consent[];
  extra: Extra;
  netAmount: number;
  taxAmount: number;
  grossAmount: number;
  createdAt: string;
  updatedAt: string;
  startsAt: string | null;
  endsAt: string | null;
}

export interface PaymentMethod {
  id: string;
  name: string;
}
