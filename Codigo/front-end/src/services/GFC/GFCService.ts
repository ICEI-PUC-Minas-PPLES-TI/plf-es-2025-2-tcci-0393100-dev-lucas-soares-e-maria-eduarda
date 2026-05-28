import { BaseService } from '../BaseService';
import type {
  GFCDTO,
  GFCSummaryDTO,
  GFCCyclomaticComplexityDTO,
  CreateGFCRequest,
  CreateGFCResponse,
  GenerateStructuralTestSignatureResponseDTO,
} from '../../features/graph/types/gfc';

const BASE = '/grafo-de-fluxo-de-controle';

class GFCService extends BaseService {
  criar = async (data: CreateGFCRequest): Promise<{ id: string }> => {
    const res = await this.post<CreateGFCResponse, CreateGFCRequest>(BASE, data);
    return { id: res.data.id_gfc };
  };

  buscarPorId = async (id: string): Promise<GFCDTO> => {
    const res = await this.get<GFCDTO>(`${BASE}/${id}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GFCSummaryDTO[]> => {
    const res = await this.get<GFCSummaryDTO[]>(`${BASE}/projeto/${projectId}`);
    return res.data;
  };

  deletar = async (id: string): Promise<void> => {
    await this.delete(`${BASE}/${id}`);
  };

  obterComplexidade = async (gfcId: string): Promise<GFCCyclomaticComplexityDTO> => {
    const res = await this.get<GFCCyclomaticComplexityDTO>(
      `${BASE}/${gfcId}/complexidade-ciclomatica`,
    );
    return res.data;
  };

  gerarAssinaturaTesteEstrutural = async (
    gfcId: string,
  ): Promise<GenerateStructuralTestSignatureResponseDTO> => {
    const res = await this.get<GenerateStructuralTestSignatureResponseDTO>(
      `${BASE}/${gfcId}/assinatura-teste-estrutural`,
    );
    return res.data;
  };
}

export default new GFCService();
