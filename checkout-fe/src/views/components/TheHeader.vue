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

      <!-- language block -->
      <LanguageSwitcher />
    </div>
  </nav>
</template>

<script setup lang="ts">
// components
import LanguageSwitcher from '@/views/components/LanguageSwitcher.vue';

// props
defineProps<{ brandName?: string | null; logo?: string | null }>();
</script>

<style scoped>
.logo {
  height: 32px;
}
</style>
