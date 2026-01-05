export interface MoneyOptions {
  currency?: string;
  locale?: string;
  currencyDisplay?: 'symbol' | 'narrowSymbol' | 'code' | 'name';
  signDisplay?: 'auto' | 'never' | 'always' | 'exceptZero';
  fractionDigitsOverride?: number;
  minimumFractionDigits?: number;
  maximumFractionDigits?: number;
}
