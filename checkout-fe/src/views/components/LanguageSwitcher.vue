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
      :active="!isBrowserMode && loc === currentLocale"
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

    <BDropdownDivider />

    <!-- BROWSER DEFAULT OPTION -->
    <BDropdownItem
      :active="isBrowserMode"
      class="d-flex align-items-center"
      @click="onUseBrowserLocale"
    >
      <span class="me-2 bi bi-globe2" aria-hidden="true" />
      <span>{{ t('languageSwitcher.browserOption') }}</span>
    </BDropdownItem>
  </BDropdown>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';

// types
import { type Locales, SUPPORTED_LOCALES } from '@/types/i18n';

// i18n
import { LOCALE_STORAGE_KEY, detectBrowserLocale } from '@/i18n';

// storage
import StorageService from '@/services/storage';

const { availableLocales, locale, t } = useI18n();

const availableLocalesTyped = computed<Locales[]>(() =>
  availableLocales.filter((loc): loc is Locales => SUPPORTED_LOCALES.includes(loc as Locales)),
);

// true when no explicit locale is persisted (using browser-based detection)
const isBrowserMode = ref(!StorageService.has(LOCALE_STORAGE_KEY));

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
  if (loc === currentLocale.value && !isBrowserMode.value) {
    return;
  }

  // store unsecured (false) — locale is not sensitive data
  StorageService.set(LOCALE_STORAGE_KEY, loc, false);
  isBrowserMode.value = false;
  currentLocale.value = loc;
}

function onUseBrowserLocale() {
  StorageService.remove(LOCALE_STORAGE_KEY);
  isBrowserMode.value = true;
  currentLocale.value = detectBrowserLocale();
}
</script>
