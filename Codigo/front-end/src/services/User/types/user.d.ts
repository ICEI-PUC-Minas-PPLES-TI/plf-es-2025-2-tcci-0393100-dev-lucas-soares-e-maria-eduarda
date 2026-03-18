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
