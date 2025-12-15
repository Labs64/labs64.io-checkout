/// <reference types="vite/client" />
interface ImportMetaEnv {
  // Router
  readonly VITE_ROUTER_HASH_MODE: string;

  // env
  readonly VITE_RUNTIME_ENV: string;
  readonly VITE_REQUIRE_RUNTIME_ENV: string;
  readonly VITE_RUNTIME_ENV_PATH: string;

  // i18n
  readonly VITE_I18N_LOCALE: string;
  readonly VITE_I18N_FALLBACK_LOCALE: string;

  // API
  readonly VITE_API_URL: string;

  // Config
  readonly VITE_TENANT_CONFIG_URL: string;

  // Security
  readonly VITE_PUBLIC_KEY: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
