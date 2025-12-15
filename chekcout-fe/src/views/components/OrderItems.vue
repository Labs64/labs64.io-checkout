<template>
  <BCard
    v-if="order"
    class="shadow-sm"
    header-class="d-flex justify-content-between align-items-center"
    body-class="ps-0 pe-0"
  >
    <template #header>
      <I18nT
        keypath="order.summary.itemsTotal"
        scope="global"
        tag="div"
        :plural="order.items.length"
        class="text-body-secondary small text-nowrap"
      >
        <template #count>
          <span class="fw-bold">{{ order.items.length }}</span>
        </template>

        <template #total>
          <span class="fw-bold">{{ money(order.netAmount) }}</span>
        </template>
      </I18nT>
    </template>

    <ul class="list-group list-group-flush">
      <li
        v-for="(item, index) in order.items"
        :key="`order-line-${index}`"
        class="list-group-item"
      >
        <div class="d-flex gap-3">
          <!-- thumbnail / placeholder -->
          <div class="flex-shrink-0 order-item-thumbnail d-flex align-items-center justify-content-center">
            <OrderItemThumbnail
              :url="item.url"
              :image="item.image"
              :name="item.name"
            />
          </div>
          <div class="flex-grow-1">
            <div class="d-flex justify-content-between align-items-start gap-2">
              <div class="me-2">
                <div class="fw-semibold mb-1">
                  <a
                    v-if="item.url"
                    :href="item.url"
                    target="_blank"
                    class="link-dark text-decoration-none"
                  >
                    {{ item.name }}
                  </a>

                  <div
                    v-else
                    class="text-dark"
                  >
                    {{ item.name }}
                  </div>
                </div>
              </div>

              <div class="text-end flex-shrink-0 text-nowrap">
                <div class="fw-semibold">
                  {{ money(item.quantity * item.price) }}
                </div>
              </div>
            </div>

            <div class="text-body-secondary small mb-1">
              {{ item.description }}
            </div>

            <div class="small mt-1">
              {{ item.quantity }}

              <template v-if="item.uom">
                <span class="opacity-75 text-lowercase">{{ item.uom }}</span>
              </template>

              × {{ money(item.price) }}
            </div>
          </div>
        </div>
      </li>
    </ul>
  </BCard>
</template>

<script setup lang="ts">
// stores
import usePurchaseOrderStore from '@/stores/purchaseOrder';

// composables
import { useMoney } from '@/composables/useMoney';

// components
import OrderItemThumbnail from '@/views/components/OrderItemThumbnail.vue';

const purchaseOrderStore = usePurchaseOrderStore();
const order = purchaseOrderStore.purchaseOrder;
const { money } = useMoney(order?.currency);
</script>

<style scoped>
.order-item-thumbnail {
  width: 64px;
  height: 64px;
}
</style>
