// composable
import useEnv from '@/composables/useEnv';
import type { TenantConfig } from '@/types/services/api/tenantConfig';

const { getEnv } = useEnv();

export async function fetchTenantConfig(tenantId: string): Promise<TenantConfig> {
  const endpoint = `${getEnv('VITE_TENANT_CONFIG_URL')}/${tenantId}`;

  console.warn(endpoint);

  // TODO(RVA): hardcoded data
  return {
    logo: 'https://www.labs64.com/img/guidechimp/guidechimp-icon-64x64.png',
    favicon: 'https://www.gstatic.com/images/branding/searchlogo/ico/favicon.ico',
    brandName: 'GuideChimp',
    theme: {
      css: '--bs-primary-rgb: red;',
    },
  };
}
