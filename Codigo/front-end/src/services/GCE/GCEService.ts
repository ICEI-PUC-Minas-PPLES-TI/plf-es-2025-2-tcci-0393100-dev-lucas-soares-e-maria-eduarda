import { BaseService } from '../BaseService';
import type {
  GCEDTO,
  CreateGCERequest,
  GCEValidationResponse,
} from '../../features/gce/types/gce';

const base = (projectId: string) => `/projeto/${projectId}/gce`;

class GCEService extends BaseService {
  criar = async (projectId: string, data: CreateGCERequest): Promise<{ id: string }> => {
    const res = await this.post<{ status: number; mensagem: string; id_gce: string }, CreateGCERequest>(base(projectId), data);
    return { id: res.data.id_gce };
  };

  buscarPorId = async (projectId: string, id: string): Promise<GCEDTO> => {
    const res = await this.get<GCEDTO>(`${base(projectId)}/${id}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GCEDTO[]> => {
    const res = await this.get<GCEDTO[]>(base(projectId));
    return res.data;
  };

  atualizar = async (projectId: string, id: string, data: CreateGCERequest): Promise<GCEDTO> => {
    const res = await this.put<GCEDTO, CreateGCERequest>(`${base(projectId)}/${id}`, data);
    return res.data;
  };

  validar = async (projectId: string, data: CreateGCERequest): Promise<GCEValidationResponse> => {
    const res = await this.post<GCEValidationResponse, CreateGCERequest>(`${base(projectId)}/validar`, data);
    return res.data;
  };

  deletar = async (projectId: string, id: string): Promise<void> => {
    await this.delete(`${base(projectId)}/${id}`);
  };
}

export default new GCEService();
