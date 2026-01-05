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
        v-if="currentLocale"
        :class="['me-1', flags[currentLocale]]"
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
          :class="['me-2', flags[loc]]"
          aria-hidden="true"
        />
        <span class="me-1 text-capitalize">{{ getLanguage(loc) }}</span>
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

const currentLocale = computed<Locales>({
  get: () => (locale.value as Locales) ?? 'en',
  set: (value) => {
    locale.value = value;
  },
});

const flags: Record<Locales, string> = {
  en: 'fi fi-gb',
  de: 'fi fi-de',
  uk: 'fi fi-ua',
};

const displayNamesCache = new Map<Locales, Intl.DisplayNames>();

function getDisplayNames(loc: Locales): Intl.DisplayNames {
  const cached = displayNamesCache.get(loc);

  if (cached) {
    return cached;
  }

  const dn = new Intl.DisplayNames([loc], { type: 'language' });
  displayNamesCache.set(loc, dn);
  return dn;
}

function getLanguage(loc: Locales) {
  return getDisplayNames(loc).of(loc) ?? loc;
}

function onChangeLocale(loc: Locales) {
  if (loc === currentLocale.value) {
    return;
  }

  currentLocale.value = loc;
}
</script>
