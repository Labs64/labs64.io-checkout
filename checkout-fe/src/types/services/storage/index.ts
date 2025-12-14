export interface StorageService {
  has(key: string): boolean;

  get<T>(key: string, def: T): T | undefined | null;

  set<T>(key: string, value: T): void;

  remove(key: string): void;
}
