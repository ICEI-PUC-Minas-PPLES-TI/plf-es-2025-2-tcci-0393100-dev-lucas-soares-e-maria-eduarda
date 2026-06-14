import { BaseService } from '../BaseService';
import type {
  User,
  CreateUserResponse,
  UserProfile,
  UpdateUserRequest,
  UpdatePasswordRequest,
  UserMutationResponse,
} from './types/user';

class UserService extends BaseService {
  public criar = async (user: User): Promise<CreateUserResponse> => {
    const response = await this.post<CreateUserResponse, User>('/usuario', user);
    return response.data;
  };

  public buscarPorId = async (id: string): Promise<UserProfile> => {
    const response = await this.get<UserProfile>(`/usuario/${id}`);
    return response.data;
  };

  public atualizar = async (id: string, data: UpdateUserRequest): Promise<UserMutationResponse> => {
    const response = await this.put<UserMutationResponse, UpdateUserRequest>(`/usuario/${id}`, data);
    return response.data;
  };

  public atualizarSenha = async (id: string, data: UpdatePasswordRequest): Promise<UserMutationResponse> => {
    const response = await this.patch<UserMutationResponse, UpdatePasswordRequest>(`/usuario/${id}/senha`, data);
    return response.data;
  };
}

export default new UserService();
