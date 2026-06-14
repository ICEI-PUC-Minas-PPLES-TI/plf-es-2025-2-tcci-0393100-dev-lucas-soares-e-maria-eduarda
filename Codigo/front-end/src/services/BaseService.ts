import http from './http';
import type { AxiosRequestConfig } from 'axios';

export abstract class BaseService {
  protected get<TRes>(url: string, config?: AxiosRequestConfig) {
    return http.get<TRes>(url, config);
  }

  protected post<TRes, TBody>(url: string, body: TBody, config?: AxiosRequestConfig) {
    return http.post<TRes>(url, body, config);
  }

  protected put<TRes, TBody>(url: string, body: TBody, config?: AxiosRequestConfig) {
    return http.put<TRes>(url, body, config);
  }

  protected patch<TRes, TBody = undefined>(url: string, body?: TBody, config?: AxiosRequestConfig) {
    return http.patch<TRes>(url, body, config);
  }

  protected delete<TRes>(url: string, config?: AxiosRequestConfig) {
    return http.delete<TRes>(url, config);
  }
}
