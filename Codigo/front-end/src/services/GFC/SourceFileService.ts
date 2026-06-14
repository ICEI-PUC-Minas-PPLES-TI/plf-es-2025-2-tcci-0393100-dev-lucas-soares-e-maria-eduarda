import { BaseService } from '../BaseService';
import type {
  GFCSourceFileDTO,
  GFCSourceMethodDTO,
  GFCSourceCodeDTO,
  GFCSourceMethodCodeDTO,
  CreateGFCSourceFileResponse,
} from '../../features/graph/types/gfc';

const base = (projectId: string) => `/projeto/${projectId}/arquivos-java`;

class SourceFileService extends BaseService {
  upload = async (projectId: string, file: File): Promise<{ id: string }> => {
    const form = new FormData();
    form.append('file', file);
    const res = await this.post<CreateGFCSourceFileResponse, FormData>(base(projectId), form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return { id: res.data.id_arquivo };
  };

  buscarPorId = async (projectId: string, sourceFileId: string): Promise<GFCSourceFileDTO> => {
    const res = await this.get<GFCSourceFileDTO>(`${base(projectId)}/${sourceFileId}`);
    return res.data;
  };

  listarPorProjeto = async (projectId: string): Promise<GFCSourceFileDTO[]> => {
    const res = await this.get<GFCSourceFileDTO[]>(base(projectId));
    return res.data;
  };

  obterCodigoFonte = async (projectId: string, sourceFileId: string): Promise<string> => {
    const res = await this.get<GFCSourceCodeDTO>(`${base(projectId)}/${sourceFileId}/source-code`);
    return res.data.sourceCode;
  };

  listarMetodos = async (projectId: string, sourceFileId: string): Promise<GFCSourceMethodDTO[]> => {
    const res = await this.get<GFCSourceMethodDTO[]>(`${base(projectId)}/${sourceFileId}/methods`);
    return res.data;
  };

  obterMetodo = async (
    projectId: string,
    sourceFileId: string,
    signature: string,
  ): Promise<GFCSourceMethodCodeDTO> => {
    const res = await this.get<GFCSourceMethodCodeDTO>(
      `${base(projectId)}/${sourceFileId}/method`,
      { params: { signature } },
    );
    return res.data;
  };

  deletar = async (projectId: string, sourceFileId: string): Promise<void> => {
    await this.delete(`${base(projectId)}/${sourceFileId}`);
  };
}

export default new SourceFileService();
