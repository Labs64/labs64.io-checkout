export function useMoney(currency?: string) {
  function money(value: number): string;
  function money(value: number, locales: string = 'en-US'): string {
    return new Intl.NumberFormat(locales, {
      style: currency ? 'currency' : 'decimal',
      currency,
      maximumFractionDigits: 2,
    }).format(value / 100);
  }

  return {
    money,
  };
}
