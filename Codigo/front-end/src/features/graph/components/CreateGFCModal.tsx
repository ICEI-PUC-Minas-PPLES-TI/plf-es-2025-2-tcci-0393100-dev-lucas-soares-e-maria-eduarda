import { useEffect, useState, type SyntheticEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { Upload, FileCode, Loader2 } from 'lucide-react';
import { Modal } from '../../../components/Modal';
import { Button } from '../../../components/Button';
import SourceFileService from '../../../services/GFC/SourceFileService';
import GFCService from '../../../services/GFC/GFCService';
import ProjectService from '../../../services/Project/ProjectService';
import { extractApiErrorMessage } from '../../../utils/apiError';
import type { ProjectDTO } from '../../../services/Project/types/project';
import type {
  GFCSourceFileDTO,
  GFCSourceMethodDTO,
  GFCSummaryDTO,
} from '../types/gfc';

interface CreateGFCModalProps {
  projectId?: string;
  onClose: () => void;
}

type Step = 'choose-file' | 'choose-method';

export function CreateGFCModal({ projectId: initialProjectId, onClose }: CreateGFCModalProps) {
  const navigate = useNavigate();

  const [selectedProjectId, setSelectedProjectId] = useState(initialProjectId ?? '');
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [loadingProjects, setLoadingProjects] = useState(!initialProjectId);

  const [step, setStep] = useState<Step>('choose-file');
  const [existingFiles, setExistingFiles] = useState<GFCSourceFileDTO[]>([]);
  const [loadingFiles, setLoadingFiles] = useState(!!initialProjectId);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);

  const [activeSourceFile, setActiveSourceFile] = useState<GFCSourceFileDTO | null>(null);
  const [methods, setMethods] = useState<GFCSourceMethodDTO[]>([]);
  const [loadingMethods, setLoadingMethods] = useState(false);
  const [existingGfcs, setExistingGfcs] = useState<GFCSummaryDTO[]>([]);
  const [selectedSignature, setSelectedSignature] = useState<string | null>(null);
  const [generating, setGenerating] = useState(false);

  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (initialProjectId) return;
    ProjectService.listarMeus()
      .then(setProjects)
      .catch(() => setError('Erro ao carregar projetos.'))
      .finally(() => setLoadingProjects(false));
  }, [initialProjectId]);

  useEffect(() => {
    if (!selectedProjectId) {
      setExistingFiles([]);
      setLoadingFiles(false);
      return;
    }
    setLoadingFiles(true);
    SourceFileService.listarPorProjeto(selectedProjectId)
      .then(setExistingFiles)
      .catch(() => setError('Erro ao carregar arquivos do projeto.'))
      .finally(() => setLoadingFiles(false));
  }, [selectedProjectId]);

  const loadMethodsFor = async (sourceFile: GFCSourceFileDTO) => {
    if (!selectedProjectId) return;
    setActiveSourceFile(sourceFile);
    setLoadingMethods(true);
    setError(null);
    try {
      const [fileMethods, gfcs] = await Promise.all([
        SourceFileService.listarMetodos(selectedProjectId, sourceFile.id),
        GFCService.listarPorProjeto(selectedProjectId),
      ]);
      setMethods(fileMethods);
      setExistingGfcs(gfcs);
      setStep('choose-method');
    } catch {
      setError('Erro ao listar métodos do arquivo.');
    } finally {
      setLoadingMethods(false);
    }
  };

  const handleUpload = async (e: SyntheticEvent) => {
    e.preventDefault();
    if (!selectedProjectId) {
      setError('Selecione um projeto.');
      return;
    }
    if (!selectedFile) {
      setError('Selecione um arquivo .java.');
      return;
    }
    setUploading(true);
    setError(null);
    try {
      const { id } = await SourceFileService.upload(selectedProjectId, selectedFile);
      const file = await SourceFileService.buscarPorId(selectedProjectId, id);
      await loadMethodsFor(file);
    } catch (err) {
      setError(extractApiErrorMessage(err, 'Erro ao enviar o arquivo.'));
    } finally {
      setUploading(false);
    }
  };

  const handleGenerate = async () => {
    if (!activeSourceFile || !selectedSignature || !selectedProjectId) return;
    const method = methods.find((m) => m.signature === selectedSignature);
    if (!method) return;

    const existing = existingGfcs.find((g) => g.methodSignature === selectedSignature);
    if (existing) {
      onClose();
      navigate(`/projeto/${selectedProjectId}/gfc/${existing.id}`);
      return;
    }

    setGenerating(true);
    setError(null);
    try {
      const { id } = await GFCService.criar(selectedProjectId, {
        projectId: selectedProjectId,
        sourceFileId: activeSourceFile.id,
        methodSignature: method.signature,
        name: method.name,
      });
      onClose();
      navigate(`/projeto/${selectedProjectId}/gfc/${id}`);
    } catch (err) {
      setError(extractApiErrorMessage(err, 'Erro ao gerar o GFC.'));
      setGenerating(false);
    }
  };

  return (
    <Modal
      title={step === 'choose-file' ? 'Novo GFC — escolha o arquivo' : 'Novo GFC — escolha o método'}
      onClose={onClose}
      maxWidth="lg"
    >
      {step === 'choose-file' && (
        <form onSubmit={handleUpload} className="space-y-4">
          {!initialProjectId && (
            <div>
              <label htmlFor="gfc-project" className="block text-sm text-gray-300 mb-1.5">
                Projeto
              </label>
              {loadingProjects ? (
                <p className="text-sm text-gray-500">Carregando projetos...</p>
              ) : (
                <select
                  id="gfc-project"
                  value={selectedProjectId}
                  onChange={(e) => {
                    setSelectedProjectId(e.target.value);
                    setSelectedFile(null);
                    setError(null);
                  }}
                  required
                  className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 focus:border-primary focus:outline-none transition-colors"
                >
                  <option value="">Selecione um projeto...</option>
                  {projects.map((p) => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              )}
            </div>
          )}

          {selectedProjectId && !loadingFiles && existingFiles.length > 0 && (
            <div>
              <p className="text-sm text-gray-300 mb-2">Arquivos já enviados</p>
              <div className="border border-edge rounded-lg divide-y divide-edge max-h-44 overflow-y-auto">
                {existingFiles.map((file) => (
                  <button
                    key={file.id}
                    type="button"
                    onClick={() => loadMethodsFor(file)}
                    disabled={loadingMethods}
                    className="w-full flex items-center gap-3 px-3 py-2 text-left hover:bg-surface-hover transition-colors disabled:opacity-50"
                  >
                    <FileCode className="w-4 h-4 text-primary-light shrink-0" />
                    <div className="min-w-0 flex-1">
                      <p className="text-sm text-gray-200 truncate font-mono">{file.fileName}</p>
                      <p className="text-xs text-gray-500">{file.language}</p>
                    </div>
                    {loadingMethods && activeSourceFile?.id === file.id && (
                      <Loader2 className="w-4 h-4 text-primary-light animate-spin shrink-0" />
                    )}
                  </button>
                ))}
              </div>
            </div>
          )}

          {selectedProjectId && (
          <div>
            <p className="text-sm text-gray-300 mb-2">
              {existingFiles.length > 0 ? 'Ou envie um novo arquivo .java' : 'Envie um arquivo .java'}
            </p>
            <label
              htmlFor="gfc-source-file"
              className="flex flex-col items-center justify-center gap-2 p-6 border-2 border-dashed border-edge rounded-lg cursor-pointer hover:border-primary transition-colors"
            >
              <Upload className="w-6 h-6 text-gray-500" />
              <span className="text-sm text-gray-300">
                {selectedFile ? selectedFile.name : 'Clique para selecionar'}
              </span>
              <span className="text-xs text-gray-500">Apenas arquivos .java</span>
              <input
                id="gfc-source-file"
                type="file"
                accept=".java"
                className="hidden"
                onChange={(e) => setSelectedFile(e.target.files?.[0] ?? null)}
              />
            </label>
          </div>
          )}

          {error && <p className="text-sm text-red-400 text-center">{error}</p>}

          <div className="flex gap-3 pt-2">
            <Button type="button" variant="ghost" className="flex-1 justify-center" onClick={onClose}>
              Cancelar
            </Button>
            <Button
              type="submit"
              className="flex-1 justify-center"
              disabled={!selectedFile || uploading}
            >
              {uploading ? 'Enviando...' : 'Enviar e continuar'}
            </Button>
          </div>
        </form>
      )}

      {step === 'choose-method' && activeSourceFile && (
        <div className="space-y-4">
          <div className="bg-surface border border-edge rounded-lg px-3 py-2">
            <p className="text-xs text-gray-500">Arquivo</p>
            <p className="text-sm font-mono text-gray-200 truncate">{activeSourceFile.fileName}</p>
          </div>

          <div>
            <p className="text-sm text-gray-300 mb-2">Métodos encontrados</p>
            {methods.length === 0 ? (
              <p className="text-sm text-gray-500 text-center py-4">
                Nenhum método encontrado nesse arquivo.
              </p>
            ) : (
              <div className="border border-edge rounded-lg divide-y divide-edge max-h-64 overflow-y-auto">
                {methods.map((method) => {
                  const existing = existingGfcs.find((g) => g.methodSignature === method.signature);
                  const isSelected = selectedSignature === method.signature;
                  return (
                    <button
                      key={method.signature}
                      type="button"
                      onClick={() => setSelectedSignature(method.signature)}
                      className={`w-full text-left px-3 py-2 transition-colors ${
                        isSelected ? 'bg-primary/15' : 'hover:bg-surface-hover'
                      }`}
                    >
                      <div className="flex items-center justify-between gap-2">
                        <span className="text-sm font-mono text-gray-200 truncate">
                          {method.name}()
                        </span>
                        {existing && (
                          <span className="text-xs text-green-400 shrink-0">Gerado</span>
                        )}
                      </div>
                      <p className="text-xs text-gray-500 font-mono mt-0.5">
                        L{method.startLine}–{method.endLine}
                      </p>
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          {error && <p className="text-sm text-red-400 text-center">{error}</p>}

          <div className="flex gap-3 pt-2">
            <Button
              type="button"
              variant="ghost"
              className="flex-1 justify-center"
              onClick={() => {
                setStep('choose-file');
                setSelectedSignature(null);
                setActiveSourceFile(null);
                setMethods([]);
              }}
              disabled={generating}
            >
              Voltar
            </Button>
            <Button
              type="button"
              className="flex-1 justify-center"
              disabled={!selectedSignature || generating}
              onClick={handleGenerate}
            >
              {generating ? 'Gerando...' : 'Gerar GFC'}
            </Button>
          </div>
        </div>
      )}
    </Modal>
  );
}
