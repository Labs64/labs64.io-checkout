import type {
  RouteLocationNormalized,
  RouteLocationNormalizedLoaded,
  NavigationGuardNext,
} from 'vue-router';

export type Middleware = (
  to: RouteLocationNormalized,
  from: RouteLocationNormalizedLoaded,
  next: NavigationGuardNext,
  ...args: unknown[]
) => void | Promise<void>;

export type MetaMiddleware = string | string[] | { [key: string]: unknown[] };

export interface RouteMiddleware {
  mw: Middleware,
  args: unknown[],
}

export interface MiddlewareConfig {
  global: {
    before: Middleware[] | [];
    after: Middleware[];
  };

  route: {
    [key: string]: Middleware | Middleware[];
  };
}
