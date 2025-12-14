let env: Partial<Record<keyof ImportMetaEnv, string>> = {};
let loaded: boolean = false;
let pending: Promise<void> | null = null;

const RUNTIME = import.meta.env.VITE_RUNTIME_ENV === 'true';
const STRICT  = import.meta.env.VITE_REQUIRE_RUNTIME_ENV === 'true';
const PATH    = import.meta.env.VITE_RUNTIME_ENV_PATH ?? '/config/env.json';

const loadEnv = async () => {
  if (loaded) {
    return;
  }

  if (pending) {
    return pending;
  }

  // skip runtime fetch entirely
  if (!RUNTIME) {
    loaded = true;
    env = {};
    return;
  }

  pending = fetch(PATH, { cache: 'no-store', headers: { 'Accept': 'application/json' } })
    .then(async (response) => {
      if (response.ok) {
        return response.json();
      }

      // for dev/local: no runtime overrides
      if (response.status === 404 && !STRICT) {
        return {};
      }

      throw new Error(`env.json HTTP ${response.status}`);
    })
    .then((data) => {
      loaded = true;
      env = data;
    })
    .finally(() => {
      pending = null;
    });

  return pending;
};


function getEnv(key: keyof ImportMetaEnv): string | undefined;
function getEnv(key: keyof ImportMetaEnv, fallback: string): string;

function getEnv(key: keyof ImportMetaEnv, fallback?: string) {
  return env[key] ?? import.meta.env[key] ?? fallback;
}

export default function useEnv() {
  return {
    isLoaded: () => loaded,
    loadEnv,
    getEnv,
  };
}
