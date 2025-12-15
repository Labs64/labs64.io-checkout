import type { TenantConfig } from '@/types/services/api/tenantConfig';

export interface TenantConfigState {
  tenantId: string | null;
  config: TenantConfig | null;

  // private
  _loading: boolean;
  _loaded: boolean;
}
