import type {
  NavigationGuardNext,
  RouteLocation,
  RouteLocationNormalized,
  RouteLocationNormalizedLoaded,
} from 'vue-router';

import { isString, isObject, forEach, castArray } from 'lodash-es';

import type { MetaMiddleware, Middleware, RouteMiddleware } from '@/types/router/middleware';

import config from './config';

export const useMiddleware = () => {
  const getRouteMiddlewares = (route: RouteLocation): RouteMiddleware[] =>
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

  const getAllRoutesMiddlewares = (route: RouteLocation): RouteMiddleware[] => {
    return [
      ...config.global.before.map((mw) => ({ mw, args: [] })),
      ...getRouteMiddlewares(route),
      ...config.global.after.map((mw) => ({ mw, args: [] })),
    ];
  };


  const nextMiddleware = (to: RouteLocationNormalized, from: RouteLocationNormalizedLoaded, next: NavigationGuardNext) => {
    const middlewares = getAllRoutesMiddlewares(to);

    const guard = (index: number = 0) => {
      const middleware = middlewares[index];


      if (!middleware) {
        return next;
      }

      return (...args: Parameters<NavigationGuardNext> | []) => {
        if (args.length) {
          return next(...args);
        }

        const { mw, args: mArgs } = middleware;
        const nextGuard = guard(index + 1) as NavigationGuardNext;
        return mw.apply({}, [to, from, nextGuard, ...mArgs]);
      };
    };

    // start middlewares
    guard();
  };

  return {
    getRouteMiddlewares,
    getAllRoutesMiddlewares,
    nextMiddleware,
  };
};

export default useMiddleware;
