import { BaseService } from '../BaseService';
import type {
  DecisionTableDTO,
  UpdateDecisionTableDetailsDTO,
  GenerateFunctionalTestSignatureResponseDTO,
} from '../../features/decision-table/types/decisionTableDTO';

const base = (projectId: string) => `/projeto/${projectId}/tabela-de-decisao`;

class DecisionTableService extends BaseService {
  criarAPartirDoGCE = async (projectId: string, gceId: string): Promise<DecisionTableDTO> => {
    const res = await this.post<DecisionTableDTO, null>(`${base(projectId)}/a-partir-do-gce/${gceId}`, null);
    return res.data;
  };

  buscarPorGceId = async (projectId: string, gceId: string): Promise<DecisionTableDTO> => {
    const res = await this.get<DecisionTableDTO>(`${base(projectId)}/gce/${gceId}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<DecisionTableDTO[]> => {
    const res = await this.get<DecisionTableDTO[]>(base(projectId));
    return res.data;
  };

  atualizar = async (projectId: string, id: string, data: UpdateDecisionTableDetailsDTO): Promise<DecisionTableDTO> => {
    const res = await this.patch<DecisionTableDTO, UpdateDecisionTableDetailsDTO>(`${base(projectId)}/${id}`, data);
    return res.data;
  };

  sincronizar = async (projectId: string, gceId: string): Promise<DecisionTableDTO> => {
    const res = await this.put<DecisionTableDTO, null>(`${base(projectId)}/sincronizar/a-partir-do-gce/${gceId}`, null);
    return res.data;
  };

  deletar = async (projectId: string, id: string): Promise<void> => {
    await this.delete(`${base(projectId)}/${id}`);
  };

  gerarAssinaturaTesteFuncional = async (
    projectId: string,
    decisionTableId: string,
  ): Promise<GenerateFunctionalTestSignatureResponseDTO> => {
    const res = await this.get<GenerateFunctionalTestSignatureResponseDTO>(
      `${base(projectId)}/${decisionTableId}/assinatura-teste-funcional`,
    );
    return res.data;
  };
}

export default new DecisionTableService();
