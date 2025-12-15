<template>
  <component
    :is="url ? 'a' : 'div'"
    v-bind="url ? { href: url, target: '_blank' } : {}"
  >
    <img
      v-if="image"
      :src="image"
      :alt="name"
      class="rounded img-fluid"
    />
    <i
      v-else
      class="bi bi-box-seam fs-4 text-secondary"
      :title="name"
      aria-hidden="true"
    />
  </component>
</template>

<script setup lang="ts">
defineProps<{
  url?: string | null;
  image?: string | null;
  name: string;
}>();
</script>
