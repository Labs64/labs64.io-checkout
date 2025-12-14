<template>
  <div class="bg-light min-vh-100">
    <TheHeader brand-name="GuideChimp" />

    <div class="container-lg py-4">
      <div class="row g-4">
        <!-- LEFT COLUMN -->
        <div class="col-12 col-lg-8">

          <!-- BILLING INFO -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">Billing Info</h2>

            <div class="card shadow-sm-sm mb-3">
              <div class="card-header d-flex justify-content-between align-items-center">
                <div class="d-flex align-items-center gap-3">
                  <i class="bi bi-person fs-4 m-2" />
                  <div class="flex-grow-1">
                    <div class="fw-semibold">
                      {{ customer.firstName }} {{ customer.lastName }}
                    </div>
                    <div class="text-body-secondary small">{{ customer.email }}</div>
                  </div>
                </div>
                <button
                  class="btn btn-link p-0"
                  @click="isBillingInfoOpened = !isBillingInfoOpened"
                >
                  <template v-if="isBillingInfoOpened">
                    <i class="bi bi-x fs-3" />
                  </template>

                  <template v-else>
                    Change
                  </template>
                </button>
              </div>
              <div
                v-if="isBillingInfoOpened"
                class="card-body"
              >
                <!-- FIRST & LAST NAME -->
                <div class="row g-2 mb-2">
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        id="firstName"
                        v-model="customer.firstName"
                        name="firstName"
                        type="text"
                        class="form-control"
                        placeholder="First Name"
                      />
                      <label for="firstName">First name *</label>
                    </div>
                  </div>
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        id="lastName"
                        v-model="customer.lastName"
                        name="lastName"
                        type="text"
                        class="form-control"
                        placeholder="Last Name"
                      />
                      <label for="lastName">Last name *</label>
                    </div>
                  </div>
                </div>

                <!-- EMAIL -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        v-model="customer.email"
                        name="email"
                        type="email"
                        class="form-control"
                        placeholder="Email"
                      />
                      <label>Email</label>
                    </div>
                  </div>
                </div>

                <!-- PHONE -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        id="phone"
                        v-model="customer.phone"
                        name="phone"
                        type="text"
                        class="form-control"
                        placeholder="Phone"
                      />
                      <label for="phone">Phone</label>
                    </div>
                  </div>
                </div>

                <!-- COUNTRY -->
                <div class="row mb-2">
                  <div class="col-md">
                    <div class="form-floating">
                      <select
                        v-model="customer.address.country"
                        class="form-select"
                      >
                        <option value="United States">United States</option>
                        <option value="Germany">Germany</option>
                        <option value="Ukraine">Ukraine</option>
                      </select>
                      <label>Country</label>
                    </div>
                  </div>
                </div>

                <!-- CITY & STATE & POSTAL CODE -->
                <div class="row g-3 mb-2">
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        id="city"
                        v-model="customer.address.city"
                        name="city"
                        type="text"
                        class="form-control"
                        placeholder="City"
                      />
                      <label for="firstName">City</label>
                    </div>
                  </div>
                  <div class="col-md">
                    <div class="form-floating">
                      <select
                        v-model="customer.address.stateCode"
                        class="form-select"
                      >
                        <option value="CT">Connecticut</option>
                        <option value="MS">Mississippi</option>
                        <option value="HI">Hawaii</option>
                      </select>
                      <label>State</label>
                    </div>
                  </div>
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        v-model="customer.address.postalCode"
                        name="postalCode"
                        type="text"
                        class="form-control"
                        placeholder="Postal code"
                      />
                      <label>Postal Code</label>
                    </div>
                  </div>
                </div>

                <!-- ADDRESS 1 -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        id="address1"
                        v-model="customer.address.address"
                        name="address"
                        type="text"
                        class="form-control"
                        placeholder="Address"
                      />
                      <label for="address1">Address 1</label>
                    </div>
                  </div>
                </div>

                <!-- ADDRESS 2 -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        id="address2"
                        name="address2"
                        type="text"
                        class="form-control"
                        placeholder="Address2"
                      />
                      <label for="address2">Address 2</label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- ORDER -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">Order</h2>

            <div class="card shadow-sm-sm">
              <div class="card-header d-flex justify-content-between align-items-center">
                <div class="text-body-secondary small text-nowrap">
                  {{ items.length }} items totaling <strong>{{ money(itemsTotal) }}</strong>
                </div>
                <button
                  class="btn btn-link p-0"
                  @click="isItemsEditable = !isItemsEditable"
                >
                  <template v-if="isItemsEditable">
                    Close
                  </template>

                  <template v-else>
                    Edit
                  </template>

                </button>
              </div>

              <div class="card-body p-0">
                <BTable
                  v-if="isItemsEditable"
                  :fields="fields"
                  :items="items"
                  borderless
                  thead-class="border-dashed border-bottom"
                  tbody-tr-class="border-bottom"
                >
                  <template #cell(id)="{ item }">
                    <div class="form-check">
                      <input class="form-check-input" type="checkbox" :value="item.id">
                    </div>
                  </template>

                  <template #cell(thumbnail)="{ item }">
                    <img
                      :src="item.thumbnail"
                      :alt="item.title"
                      class="product-thumb"
                    />
                  </template>

                  <template #cell(title)="{ item }">
                    <div class="flex-grow-1">
                      <div class="fw-semibold">{{ item.title }}</div>
                      <div class="text-body-secondary small">
                        {{ item.description }}
                      </div>
                      <div class="small mt-1">
                        1 × {{ money(item.price) }}
                      </div>
                    </div>
                  </template>

                  <template #cell(price)="{ item }">
                    <div class="fw-semibold">
                      {{ money(item.price) }}
                    </div>
                  </template>

                  <template #cell(quantity)="{ item }">
                    <input
                      v-model="item.quantity"
                      type="number"
                      class="form-control form-control-sm"
                    />
                  </template>

                  <template #cell(total)="{ item }">
                    <div class="fw-semibold">
                      {{ money(item.quantity * item.price) }}
                    </div>
                  </template>

                  <template #cell(actions)="{ item }">
                    <button class="btn btn-link">
                      <i class="bi bi-trash3" />
                    </button>
                  </template>
                </BTable>

                <ul
                  v-else
                  class="list-group list-group-flush"
                >
                  <li v-for="item in items" :key="item.id" class="list-group-item">
                    <div class="d-flex gap-3">
                      <img
                        :src="item.thumbnail"
                        :alt="item.title"
                        class="product-thumb "
                      />
                      <div class="flex-grow-1">
                        <div class="fw-semibold">{{ item.title }}</div>
                        <div class="text-body-secondary small">
                          {{ item.description }}
                        </div>
                        <div class="small mt-1">
                          1 × {{ money(item.price) }}
                        </div>
                      </div>
                      <div class="text-end">
                        <div class="fw-semibold">
                          {{ money(item.price) }}
                        </div>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </section>

          <!-- SHIPPING -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">SHIPPING</h2>

            <div class="card shadow-sm-sm">
              <div class="card-header d-flex justify-content-between align-items-center">
                <div class="d-flex align-items-center gap-3">
                  <i class="bi bi-truck fs-4 m-2" />
                  <div>
                    <div class="fw-semibold">{{ customer.firstName }} {{ customer.lastName }}</div>
                    <div class="text-body-secondary small">
                      {{ customer.address.address }}, {{ customer.address.city }}, {{ customer.address.stateCode }}
                      {{ customer.address.postalCode }}, {{ customer.address.country }}
                    </div>
                    <div class="text-body-secondary small">
                      {{ customer.phone }}
                    </div>
                  </div>
                </div>
                <button
                  class="btn btn-link p-0"
                  @click="isShippingInfoOpened = !isShippingInfoOpened"
                >
                  <template v-if="isShippingInfoOpened">
                    <i class="bi bi-x fs-3" />
                  </template>

                  <template v-else>
                    Change
                  </template>
                </button>
              </div>

              <div
                v-if="isShippingInfoOpened"
                class="card-body"
              >
                <div class="row md-2">
                  <div class="text-end">
                    <button class="btn btn-link">
                      Copy from billing info?
                    </button>
                  </div>
                </div>

                <!-- FIRST & LAST NAME -->
                <div class="row g-2 mb-2">
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        id="firstName"
                        v-model="customer.firstName"
                        name="firstName"
                        type="text"
                        class="form-control"
                        placeholder="First Name"
                      />
                      <label for="firstName">First name</label>
                    </div>
                  </div>
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        id="lastName"
                        v-model="customer.lastName"
                        name="lastName"
                        type="text"
                        class="form-control"
                        placeholder="Last Name"
                      />
                      <label for="lastName">Last name</label>
                    </div>
                  </div>
                </div>

                <!-- PHONE -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        id="phone"
                        v-model="customer.phone"
                        name="phone"
                        type="text"
                        class="form-control"
                        placeholder="Phone"
                      />
                      <label for="phone">Phone</label>
                    </div>
                  </div>
                </div>

                <!-- COUNTRY -->
                <div class="row mb-2">
                  <div class="col-md">
                    <div class="form-floating">
                      <select
                        v-model="customer.address.country"
                        class="form-select"
                      >
                        <option value="United States">United States</option>
                        <option value="Germany">Germany</option>
                        <option value="Ukraine">Ukraine</option>
                      </select>
                      <label>Country *</label>
                    </div>
                  </div>
                </div>

                <!-- CITY & STATE & POSTAL CODE -->
                <div class="row g-3 mb-2">
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        id="city"
                        v-model="customer.address.city"
                        name="city"
                        type="text"
                        class="form-control"
                        placeholder="City"
                      />
                      <label for="firstName">City *</label>
                    </div>
                  </div>
                  <div class="col-md">
                    <div class="form-floating">
                      <select
                        v-model="customer.address.stateCode"
                        class="form-select"
                      >
                        <option value="CT">Connecticut</option>
                        <option value="MS">Mississippi</option>
                        <option value="HI">Hawaii</option>
                      </select>
                      <label>State *</label>
                    </div>
                  </div>
                  <div class="col-md">
                    <div class="form-floating">
                      <input
                        v-model="customer.address.postalCode"
                        name="postalCode"
                        type="text"
                        class="form-control"
                        placeholder="Postal code"
                      />
                      <label>Postal Code *</label>
                    </div>
                  </div>
                </div>

                <!-- ADDRESS 1 -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        id="address1"
                        v-model="customer.address.address"
                        name="address"
                        type="text"
                        class="form-control"
                        placeholder="Address"
                      />
                      <label for="address1">Address 1</label>
                    </div>
                  </div>
                </div>

                <!-- ADDRESS 2 -->
                <div class="row mb-2">
                  <div class="col">
                    <div class="form-floating">
                      <input
                        id="address2"
                        name="address2"
                        type="text"
                        class="form-control"
                        placeholder="Address2"
                      />
                      <label for="address2">Address 2</label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- PAYMENT -->
          <section class="mb-3">
            <h2 class="h5 mb-3 text-uppercase fw-semibold">PAYMENT</h2>

            <div class="card shadow-sm-sm">
              <div class="card-body">
                <div class="vstack gap-2">
                  <div class="form-check d-flex align-items-center gap-2">
                    <input
                      type="radio"
                      name="payMethod"
                      class="form-check-input"
                      value="paypal"
                    >
                    <label class="form-check-label d-flex align-items-center gap-2">
                      <i class="bi bi-paypal fs-5" />
                      <span class="fw-semibold">PayPal</span>
                    </label>
                  </div>

                  <div class="hr-line-dashed m-1" />

                  <div class="form-check d-flex align-items-center gap-2">
                    <input
                      type="radio"
                      name="payMethod"
                      class="form-check-input"
                      value="card"
                    >
                    <label class="form-check-label d-flex align-items-center gap-2">
                      <i class="bi bi-stripe fs-5" />
                      <span class="fw-semibold">Stripe</span>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- RIGHT COLUMN (SUMMARY) -->
        <aside class="col-12 col-lg-4">

          <!-- Promo -->
          <div class="card shadow-sm-sm mb-3">
            <div
              class="card-header d-flex justify-content-between align-items-center"
              :class="{ 'border-bottom-0': !isPromoCodeOpened }"
            >
              <span class="fw-semibold">Promo</span>
              <button
                type="button"
                class="btn btn-link p-0"
                @click="isPromoCodeOpened = !isPromoCodeOpened"
              >
                <i class="bi bi-plus" /> Add
              </button>
            </div>
            <div
              v-if="isPromoCodeOpened"
              class="card-body"
            >
              <input type="text" class="form-control" placeholder="Enter promo code" autocomplete="off" />
            </div>
          </div>

          <!-- Summary -->
          <div class="card shadow-sm-sm position-sticky top-20">
            <div class="card-body">
              <div></div>
              <div class="d-flex justify-content-between">
                <span class="text-body-secondary fw-semibold">Subtotal({{ items.length }})</span>
                <span class="fw-semibold">{{ money(itemsTotal) }}</span>
              </div>
              <div class="d-flex justify-content-between">
                <span class="text-body-secondary">Est. delivery and setup</span>
                <span class="text-success">Included</span>
              </div>
              <div class="d-flex justify-content-between">
                <span class="text-body-secondary">Estimated Tax</span>
                <span class="fw-semibold">-</span>
              </div>
              <hr>
              <div class="d-flex justify-content-between align-items-center">
                <span class="fw-semibold">Estimated total</span>
                <span class="fs-5 fw-bold">{{ money(itemsTotal) }}</span>
              </div>
              <button class="btn btn-success w-100 mt-3">
                Confirm order
              </button>
              <ul class="small text-body-secondary mt-3 mb-0 ps-3">
                <li>By confirming the order, I accept the terms and conditions.</li>
                <li>I agree to the data processing policy and user agreement.</li>
              </ul>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <TheFooter />
  </div>
</template>

<script setup async lang="ts">
import { defineOptions, computed, ref } from 'vue';
import axios from 'axios';

// components
import TheHeader from '@/views/components/TheHeader.vue';
import TheFooter from '@/views/components/TheFooter.vue';

// options
defineOptions({ name: 'CheckoutPage' });

interface Customer {
  firstName: string;
  lastName: string;
  phone: string;
  email: string;
  address: {
    address: string;
    country: string;
    city: string;
    state: string;
    stateCode: string;
    postalCode: string;
  };
}

const { data: customerData } = await axios.get<Customer>('https://dummyjson.com/users/1');
const customer = ref<Customer>(customerData);

interface Item {
  id: number;
  title: string;
  description: string;
  price: number;
  quantity: number;
  thumbnail: string;
}

const fields = [
  {
    key: 'id',
    label: '',
    thClass: 'align-middle',
    tdClass: 'align-middle',
    thStyle: { width: '1%' },
  },
  {
    key: 'thumbnail', label: '',
    thClass: 'align-middle',
    tdClass: 'align-middle',
  },
  {
    key: 'title',
    label: '',
  },
  {
    key: 'price',
    label: 'Price',
    thClass: 'align-middle text-uppercase',
    tdClass: 'align-middle',
  },
  {
    key: 'quantity',
    label: 'Quantity',
    thStyle: { width: '5em' },
    thClass: 'align-middle text-uppercase',
    tdClass: 'align-middle',
  },
  {
    key: 'total',
    label: 'Total',
    thClass: 'align-middle text-uppercase',
    tdClass: 'align-middle',
  },
  {
    key: 'actions',
    label: '',
    thClass: 'align-middle',
    tdClass: 'align-middle',
    thStyle: { width: '1%' },
  },
];

const { data: productsData } = await axios.get<{ products: Item[] }>('https://dummyjson.com/products?limit=4');
const items = ref<Item[]>(productsData.products);

items.value.forEach((i) => {
  i.quantity = 1;
});


const itemsTotal = computed(() => items.value.reduce((sum, i) => sum + i.price, 0));

const isBillingInfoOpened = ref(false);
const isShippingInfoOpened = ref(false);
const isPromoCodeOpened = ref(false);
const isItemsEditable = ref(false);

const money = (n: number) => {
  return new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
    maximumFractionDigits: 2,
  }).format(n);
};
</script>

<style scoped>
.top-20 {
  top: 20px;
}

.shadow-sm-sm {
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, .03);
}

.product-thumb {
  width: 88px;
  height: 88px;
}
</style>
