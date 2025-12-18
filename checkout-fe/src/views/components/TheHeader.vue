<template>
  <nav class="navbar bg-white border-bottom">
    <div class="container-lg d-flex align-items-center gap-3">
      <img
        v-if="logo"
        :src="logo"
        :alt="brandName || ''"
        class="logo"
        aria-label="brand placeholder"
      />

      <div class="fw-semibold text-nowrap">
        {{ brandName }}
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';

// props
defineProps<{ brandName?: string | null; logo?: string | null }>();
</script>

<style scoped>
.logo {
  height: 32px;
}
</style>
