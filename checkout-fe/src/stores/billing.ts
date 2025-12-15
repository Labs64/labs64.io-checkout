// pinia
import { defineStore } from 'pinia';

// types
import type { BillingForm, BillingState as State } from '@/types/stores/billing';
import type { PurchaseOrder } from '@/types/services/api/backend';

// utils
import { firstNonEmptyString } from '@/utils/firstNonEmpty';

export default defineStore('billing', {
  persist: {
    form: 'remember:plain',
  },

  state(): State {
    return {
      form: {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        city: '',
        country: '',
        address1: '',
        address2: '',
        postalCode: '',
        state: '',
        vatId: '',
      },
    };
  },

  actions: {
    /**
     * Prefill billing forms from multiple sources in this priority:
     * 1) already saved value in form (remember me: PersistPlugin)
     * 2) customer.billingInfo
     * 3) customer (firstName / lastName / email)
     */
    init(po: PurchaseOrder) {
      const customer = po.customer;
      const billingInfo = customer?.billingInfo;
      const form: BillingForm = this.form;

      form.firstName = firstNonEmptyString(form.firstName, billingInfo?.firstName, customer?.firstName);
      form.lastName = firstNonEmptyString(form.lastName, billingInfo?.lastName, customer?.lastName);
      form.email = firstNonEmptyString(form.email, billingInfo?.email, customer?.email);
      form.phone = firstNonEmptyString(form.phone, billingInfo?.phone, customer?.phone);
      form.country = firstNonEmptyString(form.country, billingInfo?.country);
      form.city = firstNonEmptyString(form.city, billingInfo?.city);
      form.address1 = firstNonEmptyString(form.address1, billingInfo?.address1);
      form.address2 = firstNonEmptyString(form.address2, billingInfo?.address2);
      form.postalCode = firstNonEmptyString(form.postalCode, billingInfo?.postalCode);
      form.state = firstNonEmptyString(form.state, billingInfo?.state);
      form.vatId = firstNonEmptyString(form.vatId, billingInfo?.vatId);
    },

    async clear() {
      this.$reset();
    },
  },
});
