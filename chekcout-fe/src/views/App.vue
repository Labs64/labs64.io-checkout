<template>
  <main>
    <Suspense>
      <RouterView />
    </Suspense>
  </main>
</template>


<script setup lang="ts">
import { RouterView } from 'vue-router';
</script>
