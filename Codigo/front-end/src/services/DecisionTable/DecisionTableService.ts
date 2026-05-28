import { BaseService } from '../BaseService';
import type {
  DecisionTableDTO,
  UpdateDecisionTableDetailsDTO,
  GenerateFunctionalTestSignatureResponseDTO,
} from '../../features/decision-table/types/decisionTableDTO';

const BASE = '/tabela-de-decisao';

class DecisionTableService extends BaseService {
  criarAPartirDoGCE = async (gceId: string): Promise<DecisionTableDTO> => {
    const res = await this.post<DecisionTableDTO, null>(`${BASE}/a-partir-do-gce/${gceId}`, null);
    return res.data;
  };

  buscarPorGceId = async (gceId: string): Promise<DecisionTableDTO> => {
    const res = await this.get<DecisionTableDTO>(`${BASE}/gce/${gceId}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<DecisionTableDTO[]> => {
    const res = await this.get<DecisionTableDTO[]>(`${BASE}/projeto/${projectId}`);
    return res.data;
  };

  atualizar = async (id: string, data: UpdateDecisionTableDetailsDTO): Promise<DecisionTableDTO> => {
    const res = await this.patch<DecisionTableDTO, UpdateDecisionTableDetailsDTO>(`${BASE}/${id}`, data);
    return res.data;
  };

  sincronizar = async (gceId: string): Promise<DecisionTableDTO> => {
    const res = await this.put<DecisionTableDTO, null>(`${BASE}/sincronizar/a-partir-do-gce/${gceId}`, null);
    return res.data;
  };

  deletar = async (id: string): Promise<void> => {
    await this.delete(`${BASE}/${id}`);
  };

  gerarAssinaturaTesteFuncional = async (
    decisionTableId: string,
  ): Promise<GenerateFunctionalTestSignatureResponseDTO> => {
    const res = await this.get<GenerateFunctionalTestSignatureResponseDTO>(
      `${BASE}/${decisionTableId}/assinatura-teste-funcional`,
    );
    return res.data;
  };
}

export default new DecisionTableService();
