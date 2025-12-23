// src/types/i18n.ts
import * as en from '@/i18n/en';

export const SUPPORTED_LOCALES = ['en', 'de', 'uk'] as const;

export type Locales = (typeof SUPPORTED_LOCALES)[number];

export type Messages = typeof en.messages;

export type Linked = (key: string, args?: unknown) => string;

export interface MessageFn {
  linked: Linked;
}
