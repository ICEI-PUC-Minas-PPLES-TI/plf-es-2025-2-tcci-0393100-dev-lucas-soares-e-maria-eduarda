import { BaseService } from '../BaseService';
import type {
  GFCDTO,
  GFCSummaryDTO,
  GFCCyclomaticComplexityDTO,
  CreateGFCRequest,
  CreateGFCResponse,
  GenerateStructuralTestSignatureResponseDTO,
} from '../../features/graph/types/gfc';

const base = (projectId: string) => `/projeto/${projectId}/gfc`;

class GFCService extends BaseService {
  criar = async (projectId: string, data: CreateGFCRequest): Promise<{ id: string }> => {
    const res = await this.post<CreateGFCResponse, CreateGFCRequest>(base(projectId), data);
    return { id: res.data.id_gfc };
  };

  buscarPorId = async (projectId: string, id: string): Promise<GFCDTO> => {
    const res = await this.get<GFCDTO>(`${base(projectId)}/${id}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GFCSummaryDTO[]> => {
    const res = await this.get<GFCSummaryDTO[]>(base(projectId));
    return res.data;
  };

  deletar = async (projectId: string, id: string): Promise<void> => {
    await this.delete(`${base(projectId)}/${id}`);
  };

  obterComplexidade = async (projectId: string, gfcId: string): Promise<GFCCyclomaticComplexityDTO> => {
    const res = await this.get<GFCCyclomaticComplexityDTO>(
      `${base(projectId)}/${gfcId}/complexidade-ciclomatica`,
    );
    return res.data;
  };

  gerarAssinaturaTesteEstrutural = async (
    projectId: string,
    gfcId: string,
  ): Promise<GenerateStructuralTestSignatureResponseDTO> => {
    const res = await this.get<GenerateStructuralTestSignatureResponseDTO>(
      `${base(projectId)}/${gfcId}/assinatura-teste-estrutural`,
    );
    return res.data;
  };
}

export default new GFCService();
