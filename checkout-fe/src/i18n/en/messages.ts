export default {
  common: {
    checkout: 'Checkout',
    change: 'Change',
    buy: 'Buy Now',

    form: {
      firstName: 'First Name',
      lastName: 'Last Name',
      email: 'Email',
      phone: 'Phone',
      vatId: 'VAT ID',
      country: 'Country',
      city: 'City',
      state: 'State/Region',
      postalCode: 'Postal Code',
      address1: 'Address 1',
      address2: 'Address 2',
    },
  },

  order: {
    title: 'Order',

    summary: {
      itemsTotal: '{count} item totaling {total} | {count} items totaling {total}',
      subtotal: 'Subtotal (excl. tax)', // netAmount
      tax: 'Tax',
      total: 'Order total',
    },
  },

  billing: {
    title: 'Billing',
    saveDetails: 'Save billing details on this device',
    copyFromShipping: 'Copy from shipping details',
  },

  shipping: {
    title: 'Shipping',
    saveDetails: 'Save shipping details on this device',
    copyFromBilling: 'Copy from billing details',
  },

  payment: {
    title: 'Payment',
  },

  error: {
    notFound: {
      title: 'Error 404',
      description: 'The page you are looking for was moved, removed or might never have existed.',
      back: 'Go back',
    },
  },
};
