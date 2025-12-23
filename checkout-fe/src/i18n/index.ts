import { createI18n } from 'vue-i18n';

// lodash
import { isEmpty } from 'lodash-es';

// locales
import * as en from '@/i18n/en';
import * as ge from '@/i18n/de';
import * as uk from '@/i18n/uk';

// types
import type { Messages, Locales } from '@/types/i18n';
import { SUPPORTED_LOCALES } from '@/types/i18n';

const DEFAULT_LOCALE: Locales = (import.meta.env.VITE_I18N_LOCALE as Locales) || 'en';
const FALLBACK_LOCALE: Locales = (import.meta.env.VITE_I18N_FALLBACK_LOCALE as Locales) || 'en';
const AUTODETECT_ENABLED = import.meta.env.VITE_I18N_AUTODETECT_LOCALE === 'true';

// detect browser language and determine the best fit
function detectUserLocale(): Locales {
  const browserLanguages = navigator.languages; // e.g., ['en-US', 'uk']

  if (!isEmpty(browserLanguages)) {
    const browserLocales = new Set(browserLanguages.map((lang) => lang.toLowerCase().split('-')[0])); // e.g., ['en', 'uk']

    for (const browserLocale of browserLocales) {
      if (browserLocale && SUPPORTED_LOCALES.includes(browserLocale as Locales)) {
        return browserLocale as Locales;
      }
    }
  }

  // fallback to a default language if the browser language is not supported
  return DEFAULT_LOCALE;
}

const initialLocale: Locales = AUTODETECT_ENABLED ? detectUserLocale() : DEFAULT_LOCALE;

const i18n = createI18n<[Messages], Locales>({
  legacy: false,
  locale: initialLocale,
  fallbackLocale: FALLBACK_LOCALE,
  warnHtmlMessage: false,
  messages: {
    en: en.messages,
    de: ge.messages,
    uk: uk.messages,
  },

  datetimeFormats: {
    en: en.datetimeFormats,
    de: ge.datetimeFormats,
    uk: uk.datetimeFormats,
  },

  pluralRules: {
    uk: uk.pluralizationRule,
  },
});

export const t = i18n.global.t;
export default i18n;
