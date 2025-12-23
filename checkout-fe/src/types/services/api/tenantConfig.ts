export interface TenantConfig {
  logo?: string | null;
  favicon?: string | null;
  brandName: string;
  theme?: {
    css?: string;
  };
}
