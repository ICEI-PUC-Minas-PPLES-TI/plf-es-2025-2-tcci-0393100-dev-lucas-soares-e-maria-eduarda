// Decodifica o payload de um JWT no cliente (sem validar a assinatura — isso é
// responsabilidade do backend). Usado só para ler claims já confiáveis do token
// que o próprio backend emitiu: userId, email (subject) e role.

export interface JwtPayload {
  sub?: string; // email do usuário
  role?: string;
  userId?: string;
  exp?: number;
}

export function decodeToken(token: string | null): JwtPayload | null {
  if (!token) return null;
  const payload = token.split('.')[1];
  if (!payload) return null;

  try {
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const json = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join(''),
    );
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}
