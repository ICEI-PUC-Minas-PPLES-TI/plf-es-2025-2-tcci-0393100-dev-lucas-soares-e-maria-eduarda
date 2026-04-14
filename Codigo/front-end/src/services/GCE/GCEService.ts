import { BaseService } from '../BaseService';
import type {
  GCEDTO,
  CreateGCERequest,
  GCEValidationResponse,
} from '../../features/gce/types/gce';

const BASE = '/grafo-de-causa-efeito';

class GCEService extends BaseService {
  criar = async (data: CreateGCERequest): Promise<{ id: string }> => {
    const res = await this.post<{ status: number; mensagem: string; id_gce: string }, CreateGCERequest>(BASE, data);
    return { id: res.data.id_gce };
  };

  buscarPorId = async (id: string): Promise<GCEDTO> => {
    const res = await this.get<GCEDTO>(`${BASE}/${id}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GCEDTO[]> => {
    const res = await this.get<GCEDTO[]>(`${BASE}/projeto/${projectId}`);
    return res.data;
  };

  atualizar = async (id: string, data: CreateGCERequest): Promise<GCEDTO> => {
    const res = await this.put<GCEDTO, CreateGCERequest>(`${BASE}/${id}`, data);
    return res.data;
  };

  validar = async (data: CreateGCERequest): Promise<GCEValidationResponse> => {
    const res = await this.post<GCEValidationResponse, CreateGCERequest>(`${BASE}/validar`, data);
    return res.data;
  };

  deletar = async (id: string): Promise<void> => {
    await this.delete(`${BASE}/${id}`);
  };
}

export default new GCEService();
