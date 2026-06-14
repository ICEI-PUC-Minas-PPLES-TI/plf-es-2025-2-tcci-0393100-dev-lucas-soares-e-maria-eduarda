export interface User {
  name: string;
  email: string;
  password: string;
}

export interface CreateUserResponse {
  status: number;
  mensagem: string;
  id_usuario: string;
}

export interface UserProfile {
  id: string;
  perfil_usuario: number | null;
  name: string;
  email: string;
  createdAt: string;
  updatedAt: string | null;
}

export interface UpdateUserRequest {
  name: string;
  email: string;
  perfil_usuario: number | null;
}

export interface UpdatePasswordRequest {
  senha_original: string;
  senha_atualizada: string;
}

export interface UserMutationResponse {
  status: number;
  mensagem: string;
  id_usuario?: string;
}
