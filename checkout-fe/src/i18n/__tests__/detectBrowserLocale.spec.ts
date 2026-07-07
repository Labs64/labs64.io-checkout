import { describe, it, expect, vi, afterEach } from 'vitest';
import { detectBrowserLocale } from '../index';

describe('detectBrowserLocale', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('returns a supported locale when browser language matches', () => {
    vi.spyOn(navigator, 'languages', 'get').mockReturnValue(['de-DE', 'de', 'en-US']);
    expect(detectBrowserLocale()).toBe('de');
  });

  it('returns a supported locale for a language without region suffix', () => {
    vi.spyOn(navigator, 'languages', 'get').mockReturnValue(['uk']);
    expect(detectBrowserLocale()).toBe('uk');
  });

  it('falls back to default (en) when no browser language is supported', () => {
    vi.spyOn(navigator, 'languages', 'get').mockReturnValue(['fr-FR', 'es']);
    expect(detectBrowserLocale()).toBe('en');
  });

  it('falls back to default (en) when browser languages list is empty', () => {
    vi.spyOn(navigator, 'languages', 'get').mockReturnValue([]);
    expect(detectBrowserLocale()).toBe('en');
  });

  it('picks the first supported locale from the browser list', () => {
    vi.spyOn(navigator, 'languages', 'get').mockReturnValue(['fr', 'uk', 'de']);
    expect(detectBrowserLocale()).toBe('uk');
  });

  it('is case-insensitive for language tags', () => {
    vi.spyOn(navigator, 'languages', 'get').mockReturnValue(['DE-de']);
    expect(detectBrowserLocale()).toBe('de');
  });
});
