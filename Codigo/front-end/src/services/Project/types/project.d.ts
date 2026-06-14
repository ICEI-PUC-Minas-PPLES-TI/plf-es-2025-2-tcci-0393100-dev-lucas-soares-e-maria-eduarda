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

export type ProjectArtifactType = 'GCE' | 'GFC' | 'DECISION_TABLE';
export type RelatedArtifactType = 'GFC_SOURCE_FILE' | 'GCE';

export interface RelatedArtifactDTO {
  type: RelatedArtifactType;
  id: string;
  name: string | null;
}

export interface ProjectArtifactDTO {
  id: string;
  type: ProjectArtifactType;
  name: string;
  createdAt: string;
  updatedAt: string | null;
  relatedArtifact: RelatedArtifactDTO | null;
}
