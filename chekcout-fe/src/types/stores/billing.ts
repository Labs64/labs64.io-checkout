export interface BillingForm {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  city: string;
  country: string;
  address1: string;
  address2: string;
  postalCode: string;
  state: string;
  vatId: string;
}

export interface BillingState {
  form: BillingForm;
}
