export function firstNonEmptyString(...args: Array<string | null | undefined>): string {
  return args.find((v) => !!v) || '';
}
