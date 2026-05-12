import { BaseService } from '../BaseService';
import type {
  GFCSourceFileDTO,
  GFCSourceMethodDTO,
  GFCSourceCodeDTO,
  CreateGFCSourceFileResponse,
} from '../../features/graph/types/gfc';

const BASE = '/grafo-de-fluxo-de-controle/source-file';

class SourceFileService extends BaseService {
  upload = async (projectId: string, file: File): Promise<{ id: string }> => {
    const form = new FormData();
    form.append('projectId', projectId);
    form.append('file', file);
    const res = await this.post<CreateGFCSourceFileResponse, FormData>(BASE, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return { id: res.data.id_arquivo };
  };

  buscarPorId = async (sourceFileId: string): Promise<GFCSourceFileDTO> => {
    const res = await this.get<GFCSourceFileDTO>(`${BASE}/${sourceFileId}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GFCSourceFileDTO[]> => {
    const res = await this.get<GFCSourceFileDTO[]>(`${BASE}/projeto/${projectId}`);
    return res.data;
  };

  obterCodigoFonte = async (sourceFileId: string): Promise<string> => {
    const res = await this.get<GFCSourceCodeDTO>(`${BASE}/${sourceFileId}/source-code`);
    return res.data.sourceCode;
  };

  listarMetodos = async (sourceFileId: string): Promise<GFCSourceMethodDTO[]> => {
    const res = await this.get<GFCSourceMethodDTO[]>(`${BASE}/${sourceFileId}/methods`);
    return res.data;
  };

  deletar = async (sourceFileId: string): Promise<void> => {
    await this.delete(`${BASE}/${sourceFileId}`);
  };
}

export default new SourceFileService();
