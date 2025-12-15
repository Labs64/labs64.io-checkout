export interface ShippingForm {
  firstName: string;
  lastName: string;
  phone: string;
  city: string;
  country: string;
  address1: string;
  address2: string;
  postalCode: string;
  state: string;
}

export interface ShippingState {
  form: ShippingForm;
}
