// lodash
import { get, isNull } from 'lodash-es';

// storage
import type { StorageService } from '@/types/services/storage';

// env
import useEnv from '@/composables/useEnv';

const { getEnv } = useEnv();

const STORAGE_KEY = getEnv('VITE_STORAGE', 'STORAGE');

type Storage = Record<string, { secured: boolean }>;

const storage: Storage = (() => {
  if (typeof localStorage === 'undefined') {
    return null;
  }

  const s = localStorage.getItem(STORAGE_KEY);
  return s ? JSON.parse(s) : {};
})();

const isSecured = (key: string): boolean => {
  if (!storage) {
    return false;
  }

  return get(storage[key], 'secured', false);
};

export class LocalStorageService implements StorageService {
  has(key: string): boolean {
    return !!(typeof localStorage !== 'undefined' && localStorage.getItem(key));
  }

  get<T>(key: string): T | null {
    if (!storage) {
      return null;
    }

    let value = localStorage.getItem(key);

    if (isNull(value)) {
      return null;
    }

    if (isSecured(key)) {
      value = atob(value);
    }

    try {
      return JSON.parse(value);
    } catch (ignored) {
      return null;
    }
  }

  set<T>(key: string, value: T, secured = true): void {
    if (!storage) {
      return;
    }

    if (key === STORAGE_KEY) {
      throw Error(`${key} is reserved`);
    }

    let v: string = JSON.stringify(value);

    if (secured) {
      v = btoa(v);
    }

    storage[key] = { secured };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(storage));
    localStorage.setItem(key, v);
  }

  remove(key: string): void {
    if (!storage) {
      return;
    }

    delete storage[key];
    localStorage.setItem(STORAGE_KEY, JSON.stringify(storage));
    localStorage.removeItem(key);
  }
}
