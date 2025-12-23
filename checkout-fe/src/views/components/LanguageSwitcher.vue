<template>
  <BDropdown
    :variant="null"
    size="sm"
    offset="5"
    toggle-class="border-0 bg-transparent p-0 d-flex align-items-center"
  >
    <!-- BUTTON -->
    <template #button-content>
      <span
        v-if="currentLanguage"
        :class="['me-1', currentLanguage.flag]"
        aria-hidden="true"
      />
    </template>

    <!-- LANGUAGES LIST -->
    <BDropdownItem
      v-for="loc in availableLocalesTyped"
      :key="`locale-${loc}`"
      class="d-flex align-items-center justify-content-between"
      @click="onChangeLocale(loc)"
    >
      <div class="d-flex align-items-center">
        <span
          :class="['me-2', getLanguage(loc)?.flag]"
          aria-hidden="true"
        />
        <span class="me-1">{{ getLanguage(loc)?.name }}</span>
        <span class="text-muted text-uppercase small"> ({{ loc }}) </span>
      </div>
    </BDropdownItem>
  </BDropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

// types
import { type Locales, SUPPORTED_LOCALES } from '@/types/i18n';

const { availableLocales, locale } = useI18n();

const availableLocalesTyped = computed<Locales[]>(() =>
  availableLocales.filter((loc): loc is Locales => SUPPORTED_LOCALES.includes(loc as Locales)),
);

const languages: Record<Locales, { flag: string; name: string }> = {
  en: { flag: 'fi fi-gb', name: 'English' },
  de: { flag: 'fi fi-de', name: 'Deutsche' },
  uk: { flag: 'fi fi-ua', name: 'Українська' },
};

const currentLocale = computed<Locales>({
  get: () => (locale.value as Locales) ?? 'en',
  set: (value) => {
    locale.value = value;
  },
});

const currentLanguage = computed(() => languages[currentLocale.value]);

function getLanguage(loc: Locales) {
  return languages[loc];
}

function onChangeLocale(loc: Locales) {
  if (loc === currentLocale.value) {
    return;
  }

  currentLocale.value = loc;
}
</script>
