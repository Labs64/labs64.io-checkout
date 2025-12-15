export type PersistOptions<T> = {
  [K in keyof T]?: 'plain' | 'secure' | 'none' | 'remember:plain' | 'remember:secure';
} & {};
