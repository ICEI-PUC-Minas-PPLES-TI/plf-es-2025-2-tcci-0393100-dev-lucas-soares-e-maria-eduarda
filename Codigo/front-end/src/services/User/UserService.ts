import { BaseService } from '../BaseService';
import type { User, CreateUserResponse } from './types/user';

class UserService extends BaseService {
 public criar = async (user: User): Promise<CreateUserResponse> => {
    const response = await this.post<CreateUserResponse, User>('/usuario', user);
    return response.data;
  };
}

export default new UserService();
