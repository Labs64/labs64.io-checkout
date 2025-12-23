<template>
  <section class="d-flex align-items-center min-vh-100">
    <div class="container">
      <div class="row align-items-center gy-5">
        <div class="col-md-6 order-md-2 text-center text-md-end">
          <img
            src="/images/404.png"
            alt="Page not found"
            class="w-100 h-100"
          />
        </div>
        <div class="col-md-6 text-center text-md-start">
          <h1 class="display-1 fw-bold text-muted mb-3">{{ t('error.notFound.title') }}</h1>
          <p class="fs-5 fw-light text-muted mb-4">
            {{ t('error.notFound.description') }}
          </p>
          <div v-if="isNotEmptyHistory">
            <button
              type="button"
              class="btn btn-secondary btn-lg px-4 d-inline-flex align-items-center gap-2"
              @click="goBack"
            >
              {{ t('error.notFound.back') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const router = useRouter();

const isNotEmptyHistory = ref(history.length > 1);

function goBack() {
  if (isNotEmptyHistory.value) {
    router.back();
  }
}
</script>
