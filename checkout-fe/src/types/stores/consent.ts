import type { Consent } from '@/types/services/api/backend';

export interface ConsentState {
  items: Consent[];
  values: Record<string, boolean>;
}
