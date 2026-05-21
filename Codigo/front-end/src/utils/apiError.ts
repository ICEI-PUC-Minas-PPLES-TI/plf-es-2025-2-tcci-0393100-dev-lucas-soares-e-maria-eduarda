import axios from 'axios';

/**
 * Extrai a mensagem de erro retornada pelo backend (campo `mensagem` do
 * `ErrorResponse` do `GlobalExceptionHandler`). Cai pro fallback se a resposta
 * não tem corpo previsível ou se for um erro de rede/cliente.
 */
export function extractApiErrorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data;
    if (data && typeof data === 'object') {
      const msg = (data as { mensagem?: unknown; message?: unknown }).mensagem
        ?? (data as { message?: unknown }).message;
      if (typeof msg === 'string' && msg.trim().length > 0) return msg;
    }
  }
  return fallback;
}
