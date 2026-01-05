import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

// types
import type { MoneyOptions } from '@/types/composables/useMoney';

// i18n locale to Intl.locale
const LOCALE_MAP: Record<string, string> = {
  uk: 'uk-UA',
  de: 'de-DE',
  en: 'en-US',
};

const formatterCache = new Map<string, Intl.NumberFormat>();
const digitsCache = new Map<string, number>();

function resolveLocale(raw: string | undefined) {
  if (!raw) {
    return 'en-US';
  }
  return LOCALE_MAP[raw] ?? raw;
}

function getFormatter(key: string, create: () => Intl.NumberFormat) {
  const cached = formatterCache.get(key);

  if (cached) {
    return cached;
  }

  const fmt = create();
  formatterCache.set(key, fmt);

  return fmt;
}

function getCurrencyDigits(locale: string, currency: string) {
  const key = `${locale}|${currency}`;
  const cached = digitsCache.get(key);

  if (cached != null) {
    return cached;
  }

  const digits = new Intl.NumberFormat(locale, { style: 'currency', currency }).resolvedOptions().maximumFractionDigits;

  if (digits) {
    digitsCache.set(key, digits);
  }

  return digits ?? 0;
}

export function useMoney(defaultCurrency?: string) {
  const { locale: i18nLocale } = useI18n();
  const activeLocale = computed(() => resolveLocale(i18nLocale.value));

  function money(value: number, opts: MoneyOptions = {}): string {
    if (value == null || Number.isNaN(value)) {
      return '—';
    }

    const loc = resolveLocale(opts.locale ?? activeLocale.value);
    const currency = opts.currency ?? defaultCurrency;

    // CURRENCY
    if (currency) {
      const digits = opts.fractionDigitsOverride ?? getCurrencyDigits(loc, currency);

      const major = value / 10 ** digits;

      const currencyDisplay = opts.currencyDisplay ?? 'narrowSymbol';
      const signDisplay = opts.signDisplay ?? 'auto';

      const cacheKey = `cur|${loc}|${currency}|${currencyDisplay}|${signDisplay}`;
      const fmt = getFormatter(
        cacheKey,
        () =>
          new Intl.NumberFormat(loc, {
            style: 'currency',
            currency,
            currencyDisplay,
            signDisplay,
          }),
      );

      return fmt.format(major);
    }

    const minFD = opts.minimumFractionDigits ?? 2;
    const maxFD = opts.maximumFractionDigits ?? 2;
    const major = value / 10 ** maxFD;

    const signDisplay = opts.signDisplay ?? 'auto';

    const cacheKey = `dec|${loc}|${minFD}|${maxFD}|${signDisplay}`;
    const fmt = getFormatter(
      cacheKey,
      () =>
        new Intl.NumberFormat(loc, {
          style: 'decimal',
          minimumFractionDigits: minFD,
          maximumFractionDigits: maxFD,
          signDisplay,
        }),
    );

    return fmt.format(major);
  }

  return { money };
}
