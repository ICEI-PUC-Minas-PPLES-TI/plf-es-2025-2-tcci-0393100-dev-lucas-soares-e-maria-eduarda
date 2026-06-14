import { BaseService } from '../BaseService';
import type {
  ProjectDTO,
  CreateProjectRequest,
  UpdateProjectRequest,
  ProjectMutationResponse,
  ProjectArtifactDTO,
} from './types/project';

class ProjectService extends BaseService {
  public listarMeus = async (): Promise<ProjectDTO[]> => {
    const response = await this.get<ProjectDTO[]>('/projeto/meus');
    return response.data;
  };

  public buscarPorId = async (id: string): Promise<ProjectDTO> => {
    const response = await this.get<ProjectDTO>(`/projeto/${id}`);
    return response.data;
  };

  public criar = async (data: CreateProjectRequest): Promise<ProjectMutationResponse> => {
    const response = await this.post<ProjectMutationResponse, CreateProjectRequest>('/projeto', data);
    return response.data;
  };

  public atualizar = async (id: string, data: UpdateProjectRequest): Promise<ProjectMutationResponse> => {
    const response = await this.put<ProjectMutationResponse, UpdateProjectRequest>(`/projeto/${id}`, data);
    return response.data;
  };

  public excluir = async (id: string): Promise<ProjectMutationResponse> => {
    const response = await this.delete<ProjectMutationResponse>(`/projeto/${id}`);
    return response.data;
  };

  public listarArtefatos = async (projectId: string): Promise<ProjectArtifactDTO[]> => {
    const response = await this.get<ProjectArtifactDTO[]>(`/projeto/${projectId}/artefatos`);
    return response.data;
  };
}

export default new ProjectService();
