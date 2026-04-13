import { BaseService } from '../BaseService';
import type {
  GCEDTO,
  CreateGCERequest,
  GCEValidationResponse,
} from '../../features/gce/types/gce';

const BASE = '/grafo-de-causa-efeito';
const INDEX_KEY = 'gce_index'; // { [projectId]: string[] }

function getIndex(): Record<string, string[]> {
  try {
    return JSON.parse(localStorage.getItem(INDEX_KEY) ?? '{}');
  } catch {
    return {};
  }
}

function addToIndex(projectId: string, gceId: string) {
  const index = getIndex();
  const existing = index[projectId] ?? [];
  if (!existing.includes(gceId)) {
    index[projectId] = [...existing, gceId];
    localStorage.setItem(INDEX_KEY, JSON.stringify(index));
  }
}

function removeFromIndex(projectId: string, gceId: string) {
  const index = getIndex();
  index[projectId] = (index[projectId] ?? []).filter((id) => id !== gceId);
  localStorage.setItem(INDEX_KEY, JSON.stringify(index));
}

class GCEService extends BaseService {
  criar = async (data: CreateGCERequest): Promise<{ id: string }> => {
    const res = await this.post<{ status: number; mensagem: string; id_gce: string }, CreateGCERequest>(BASE, data);
    addToIndex(data.projectId, res.data.id_gce);
    return { id: res.data.id_gce };
  };

  buscarPorId = async (id: string): Promise<GCEDTO> => {
    const res = await this.get<GCEDTO>(`${BASE}/${id}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GCEDTO[]> => {
    const ids = getIndex()[projectId] ?? [];
    if (ids.length === 0) return [];
    const results = await Promise.allSettled(ids.map((id) => this.buscarPorId(id)));
    return results
      .filter((r): r is PromiseFulfilledResult<GCEDTO> => r.status === 'fulfilled')
      .map((r) => r.value);
  };

  atualizar = async (id: string, data: CreateGCERequest): Promise<GCEDTO> => {
    const res = await this.put<GCEDTO, CreateGCERequest>(`${BASE}/${id}`, data);
    return res.data;
  };

  validar = async (id: string): Promise<GCEValidationResponse> => {
    const res = await this.get<GCEValidationResponse>(`${BASE}/${id}/validar`);
    return res.data;
  };

  // TODO: substituir pelo endpoint real quando o backend implementar DELETE
  deletar = async (projectId: string, id: string): Promise<void> => {
    removeFromIndex(projectId, id);
  };
}

export default new GCEService();
