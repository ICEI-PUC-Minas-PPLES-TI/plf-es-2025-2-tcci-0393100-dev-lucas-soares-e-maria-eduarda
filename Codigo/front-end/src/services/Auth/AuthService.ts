import { BaseService } from '../BaseService';
import type { LoginRequest, LoginResponse } from './types/auth';

class AuthService extends BaseService {
  public login = async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await this.post<LoginResponse, LoginRequest>('/login', credentials);
    return response.data;
  };
}

export default new AuthService();