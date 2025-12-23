import type { RouteLocationNormalized, RouteLocationRaw } from 'vue-router';

export function toNotFoundRoute(route: RouteLocationNormalized): RouteLocationRaw {
  return {
    name: '404Page',
    params: {
      pathMatch: route.path.replace(/^\/+/, '').split('/'),
    },
    query: route.query,
    hash: route.hash,
  };
}
