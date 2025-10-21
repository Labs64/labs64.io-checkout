/// <reference types="vite/client" />
interface ImportMetaEnv {
  // Router
  readonly VITE_ROUTER_HASH_MODE: string;

  // env
  readonly VITE_RUNTIME_ENV: string;
  readonly VITE_REQUIRE_RUNTIME_ENV: string;
  readonly VITE_RUNTIME_ENV_PATH: string;

  // API
  readonly VITE_BASE_API_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
