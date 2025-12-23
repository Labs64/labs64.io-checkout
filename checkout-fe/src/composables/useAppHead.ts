import { useHead, type UseHeadInput, type UseHeadOptions } from '@unhead/vue';

// i18n
import { useI18n } from 'vue-i18n';

// stores
import useTenantConfigStore from '@/stores/tenantConfig';

export function useAppHead<I = UseHeadInput>(input?: UseHeadInput, options?: UseHeadOptions) {
  const tenantConfigStore = useTenantConfigStore();
  const { config } = tenantConfigStore;

  const favicon = config?.favicon || './favicon.ico';

  const { locale } = useI18n();

  const defaultInput = {
    link: [{ rel: 'icon', href: favicon, sizes: 'any' }],
    htmlAttrs: { lang: locale },
  };

  return useHead({
    ...defaultInput,
    ...input,
  });
}
