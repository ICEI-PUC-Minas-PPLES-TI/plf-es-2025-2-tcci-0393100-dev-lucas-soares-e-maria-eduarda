export interface ProjectDTO {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string | null;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
}

export interface UpdateProjectRequest {
  name: string;
  description: string;
}

export interface ProjectMutationResponse {
  status: number;
  mensagem: string;
  id_projeto: string;
}
