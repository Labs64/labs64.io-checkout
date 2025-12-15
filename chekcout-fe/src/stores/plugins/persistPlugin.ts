// vue
import { ref, watch } from 'vue';

// lodash
import { forEach, isEmpty, isObject } from 'lodash-es';

// pinia
import type { PiniaPluginContext } from 'pinia';

// storage
import StorageService from '@/services/storage';
import { LocalStorageService } from '@/services/storage/LocalStorageService';

// types
import type { PersistOptions } from '@/types/stores/plugins/persistPlugin';

const REMEMBER_PREFIX = 'remember:';
const PERSIST_STORAGE_KEY = `PERSIST_PLUGIN`;

const internalStorage = new LocalStorageService();

const saveRemember = (storageID: string, state: boolean) => {
  const storage = internalStorage.get<Record<string, boolean>>(PERSIST_STORAGE_KEY) || {};
  storage[storageID] = state;
  internalStorage.set(PERSIST_STORAGE_KEY, storage);
};

const getRemember = (storageID: string) => {
  const storage = internalStorage.get<Record<string, boolean>>(PERSIST_STORAGE_KEY);
  return storage?.[storageID] || false;
};

export default ({ store, options }: PiniaPluginContext) => {
  const persist: PersistOptions<typeof store.$state> = options.persist || {};
  const remember = ref(false);

  const storageID = store.$id.trim();

  remember.value = getRemember(storageID);

  const getStorageKey = (key: string): string => `${storageID}.${key.trim()}`;
  const parseMode = (mode?: string) => {
    if (!mode) {
      return { baseMode: undefined, isRemember: false };
    }

    const isRemember = mode.startsWith(REMEMBER_PREFIX);
    const baseMode = isRemember ? mode.slice(REMEMBER_PREFIX.length) : mode;

    return { baseMode, isRemember };
  };

  const shouldPersistKey = (key: string, mode?: string): boolean => {
    if (!(key in store.$state)) {
      console.warn(
        `[Persist Plugin] Key "${key}" from persist options does not exist in the "${store.$id}" store state.`,
      );
      return false;
    }

    return !!mode && mode !== 'none';
  };

  if (!isEmpty(persist)) {
    // set values from storage when initialization
    forEach(persist, (mode, key) => {
      const { baseMode } = parseMode(mode);

      if (!shouldPersistKey(key, baseMode)) {
        return;
      }

      const keyStorage = getStorageKey(key);

      if (StorageService.has(keyStorage)) {
        const storedValue = StorageService.get(keyStorage);

        store.$patch((state) => {
          state[key] = storedValue;
        });
      }
    });

    store.$subscribe((_, state) => {
      forEach(persist, (mode, key) => {
        const { baseMode, isRemember } = parseMode(mode);

        if (!shouldPersistKey(key, baseMode) || (isRemember && !remember.value)) {
          return;
        }

        const keyStorage = getStorageKey(key);
        const value = state[key];

        const isEmptyValue = !value || (isObject(value) && isEmpty(value));

        if (isEmptyValue) {
          StorageService.remove(keyStorage);
        } else {
          StorageService.set(keyStorage, value, baseMode === 'secure');
        }
      });
    });

    watch(
      remember,
      (enabled) => {
        saveRemember(storageID, enabled);

        forEach(persist, (mode, key) => {
          const { baseMode, isRemember } = parseMode(mode);
          const keyStorage = getStorageKey(key);
          const value = store.$state[key];

          if (!shouldPersistKey(key, baseMode)) {
            return;
          }

          if (!isRemember) {
            return;
          }

          const isEmptyValue = !value || (isObject(value) && isEmpty(value));

          if (!enabled || isEmptyValue) {
            StorageService.remove(keyStorage);
          } else {
            StorageService.set(keyStorage, value, baseMode === 'secure');
          }
        });
      },
      { immediate: true },
    );
  }

  return { $persistRemember: remember as unknown as boolean };
};
