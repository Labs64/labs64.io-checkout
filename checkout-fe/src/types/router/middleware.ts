import type {
  RouteLocationNormalized,
  RouteLocationNormalizedLoaded,
  NavigationGuardReturn,
  _Awaitable,
} from 'vue-router';

export type Middleware = (
  to: RouteLocationNormalized,
  from: RouteLocationNormalizedLoaded,
  ...args: unknown[]
) => _Awaitable<NavigationGuardReturn>;

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
