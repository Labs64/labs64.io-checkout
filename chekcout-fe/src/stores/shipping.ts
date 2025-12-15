// pinia
import { defineStore } from 'pinia';

// types
import type { ShippingForm, ShippingState as State } from '@/types/stores/shipping';
import type { PurchaseOrder } from '@/types/services/api/backend';

// utils
import { firstNonEmptyString } from '@/utils/firstNonEmpty';

export default defineStore('shipping', {
  persist: {
    form: 'remember:plain',
  },

  state(): State {
    return {
      form: {
        firstName: '',
        lastName: '',
        phone: '',
        city: '',
        country: '',
        address1: '',
        address2: '',
        postalCode: '',
        state: '',
      },
    };
  },

  actions: {
    init(po: PurchaseOrder) {
      const customer = po.customer;
      const shippingInfo = customer?.shippingInfo;
      const form: ShippingForm = this.form;

      form.firstName = firstNonEmptyString(form.firstName, shippingInfo?.firstName, customer?.firstName);
      form.lastName = firstNonEmptyString(form.lastName, shippingInfo?.lastName, customer?.lastName);
      form.phone = firstNonEmptyString(form.phone, shippingInfo?.phone, customer?.phone);
      form.country = firstNonEmptyString(form.country, shippingInfo?.country);
      form.city = firstNonEmptyString(form.city, shippingInfo?.city);
      form.address1 = firstNonEmptyString(form.address1, shippingInfo?.address1);
      form.address2 = firstNonEmptyString(form.address2, shippingInfo?.address2);
      form.postalCode = firstNonEmptyString(form.postalCode, shippingInfo?.postalCode);
      form.state = firstNonEmptyString(form.state, shippingInfo?.state);
    },

    async clear() {
      this.$reset();
    },
  },
});
