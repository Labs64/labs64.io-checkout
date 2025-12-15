import type { PaymentMethod } from '@/types/services/api/backend';

export interface PaymentState {
  methods: PaymentMethod[];
  selectedMethod: PaymentMethod | null;

  // private
  _loading: boolean;
  _loaded: boolean;
}
