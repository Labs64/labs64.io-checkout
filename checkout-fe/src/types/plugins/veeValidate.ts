export interface ValidatorRef {
  validate(): Promise<ValidationResult>;
  meta: {
    valid: boolean;
  };
}

export interface ValidationResult<T extends string = string> {
  valid: boolean;
  errors: Record<string, string>[];
  results: Record<T, { valid: boolean; errors: string[] }>;
  source: 'fields';
  values: Record<string, unknown>;
}
