<template>
  <nav class="navbar bg-white border-bottom">
    <div class="container-lg d-flex align-items-center gap-3">
      <div class="placeholder-logo" aria-label="brand placeholder"></div>

      <div class="fw-semibold text-nowrap">
        {{ brandName }}
      </div>

      <span class="fw-medium">CHECKOUT</span>
    </div>
  </nav>
</template>


<script setup lang="ts">
import { defineProps } from 'vue';

// props
defineProps<{ brandName?: string, logo?: string }>();
</script>


<style scoped>
.placeholder-logo {
  width: 32px;
  height: 32px;
  background: var(--bs-secondary-bg);
  border: 1px dashed var(--bs-border-color);
}
</style>
