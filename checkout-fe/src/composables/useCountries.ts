import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import countries2to3 from 'countries-list/minimal/countries.2to3.min.json';

type Option = { code: string; name: string };

const cache = new Map<string, Intl.DisplayNames>();

function regionNames(loc: string): Intl.DisplayNames {
  const key = `region:${loc}`;
  const cached = cache.get(key);
  if (cached) return cached;

  const dn = new Intl.DisplayNames([loc], { type: 'region' });
  cache.set(key, dn);
  return dn;
}

export function useCountries() {
  const { locale } = useI18n();

  const countries = computed<Option[]>(() => {
    const dn = regionNames(locale.value);
    const collator = new Intl.Collator(locale.value);

    const options: Option[] = Object.keys(countries2to3).map((alpha2) => {
      const code = alpha2.toUpperCase();
      return { code, name: dn.of(code) ?? code };
    });

    options.sort((a, b) => collator.compare(a.name, b.name));
    return options;
  });

  return { countries };
}
