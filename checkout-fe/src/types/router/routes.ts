import type { RouteMeta as VueRouteMeta, RouteRecordRaw as VueRouteRecordRaw } from 'vue-router';

export type RouteMiddleware = string | string[] | { [key: string]: unknown[] };

export type RouteMeta = VueRouteMeta & {
  middleware?: RouteMiddleware;
};

export type RouteRecordRaw = VueRouteRecordRaw & {
  meta?: RouteMeta;
};

export type Routes = RouteRecordRaw[];
