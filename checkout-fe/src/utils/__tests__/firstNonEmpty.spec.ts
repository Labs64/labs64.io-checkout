import { describe, it, expect } from 'vitest';
import { firstNonEmptyString } from '../firstNonEmpty';

describe('firstNonEmptyString', () => {
  it('returns the first non-empty string', () => {
    expect(firstNonEmptyString('', null, undefined, 'abc', 'def')).toBe('abc');
  });

  it('returns empty string if all values are empty/null/undefined', () => {
    expect(firstNonEmptyString('', null, undefined)).toBe('');
  });

  it('returns empty string if no args provided', () => {
    expect(firstNonEmptyString()).toBe('');
  });

  it('treats whitespace-only string as non-empty (current behavior)', () => {
    expect(firstNonEmptyString('', '   ', 'abc')).toBe('   ');
  });
});
