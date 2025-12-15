import en from '@/i18n/en';

export type Locales = 'en';

export type Messages = typeof en;

export type Linked = (key: string, args?: unknown) => string;

export interface MessageFn {
  linked: Linked;
}
