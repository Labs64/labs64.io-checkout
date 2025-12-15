import { createI18n } from 'vue-i18n';

// locales
import en from '@/i18n/en';

// types
import type { Messages, Locales } from '@/types/i18n';

const i18n = createI18n<[Messages], Locales>({
  legacy: false,
  locale: import.meta.env.VITE_I18N_LOCALE,
  fallbackLocale: import.meta.env.VITE_I18N_FALLBACK_LOCALE,
  warnHtmlMessage: false,
  messages: {
    en,
  },

  datetimeFormats: {
    en: {
      short: {
        day: '2-digit', // 09
        month: '2-digit', // 05
        year: 'numeric', // 2025
      },
      long: {
        weekday: 'long', // Friday
        day: '2-digit', // 09
        month: 'long', // May
        year: 'numeric', // 2025
        hour: '2-digit', // 14
        minute: '2-digit', // 05
        second: '2-digit', // 22
        hour12: false, // 24-hour format
      },
      iso: {
        year: 'numeric', // 2025
        month: '2-digit', // 05
        day: '2-digit', // 09
        hour: '2-digit', // 14
        minute: '2-digit', // 05
        hour12: false, // 24-hour format
      },
    },
  },
});

export const t = i18n.global.t;

export default i18n;
