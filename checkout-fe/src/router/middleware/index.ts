import type {
  RouteLocation,
  RouteLocationNormalized,
  RouteLocationNormalizedLoaded,
} from 'vue-router';

import { isString, isObject, forEach, castArray } from 'lodash-es';

import type { MetaMiddleware, RouteMiddleware } from '@/types/router/middleware';

import config from './config';

export const useRouteMiddleware = () => {
  const collectRouteMiddleware = (route: RouteLocation): RouteMiddleware[] =>
    route.matched.flatMap(({ meta }) => {
      let metaMiddleware = meta?.middleware as MetaMiddleware | undefined;

      if (!metaMiddleware) {
        return [];
      }

      const result: RouteMiddleware[] = [];

      if (isString(metaMiddleware)) {
        metaMiddleware = [metaMiddleware];
      }

      forEach(metaMiddleware, (v, k) => {
        let name = '';
        let args: unknown[] = [];

        if (isObject(v)) {
          name = k;
          args = v as unknown[];
        }

        if (isString(v)) {
          const [first, second] = v.split(':') as [string, string | undefined];
          name = first;
          args = second ? second.split(',') : [];
        }

        let middleware = config.route[name];

        if (!middleware) {
          console.error(`Middleware "${name}" not found.`);
          return;
        }

        middleware = castArray(middleware);

        result.push(...middleware.map((mw): RouteMiddleware => ({ mw, args })));
      });

      return result;
    });

  const buildMiddlewarePipeline = (route: RouteLocation): RouteMiddleware[] => {
    return [
      ...config.global.before.map((mw) => ({ mw, args: [] })),
      ...collectRouteMiddleware(route),
      ...config.global.after.map((mw) => ({ mw, args: [] })),
    ];
  };

  const navigationGuard = async (to: RouteLocationNormalized, from: RouteLocationNormalizedLoaded) => {
    const middlewares = buildMiddlewarePipeline(to);

    if (!middlewares) {
      return;
    }

    for (let i = 0; i < middlewares.length; i++) {
      const middleware = middlewares[i];

      if (!middleware) {
        continue;
      }

      const result = await middleware.mw.apply({}, [to, from, ...middleware.args]);

      if (result !== undefined) {
        return result;
      }
    }
  };

  return {
    collectRouteMiddleware,
    buildMiddlewarePipeline,
    navigationGuard,
  };
};

export default useRouteMiddleware;
