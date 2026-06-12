import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, FileCode, Trash2, Eye } from 'lucide-react';
import { Button } from '../../../components/Button';
import { ConfirmModal } from '../../../components/ConfirmModal';
import { SourceFileViewerModal } from './SourceFileViewerModal';
import SourceFileService from '../../../services/GFC/SourceFileService';
import GFCService from '../../../services/GFC/GFCService';
import { formatRelativeDate } from '../../../utils/formatDate';
import type {
  GFCSourceFileDTO,
  GFCSourceMethodDTO,
  GFCSummaryDTO,
} from '../types/gfc';

interface SourceFileListProps {
  projectId: string;
}

export function SourceFileList({ projectId }: SourceFileListProps) {
  const navigate = useNavigate();
  const [files, setFiles] = useState<GFCSourceFileDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<GFCSourceFileDTO | null>(null);

  const [openedFile, setOpenedFile] = useState<GFCSourceFileDTO | null>(null);
  const [openedMethods, setOpenedMethods] = useState<GFCSourceMethodDTO[]>([]);
  const [projectGfcs, setProjectGfcs] = useState<GFCSummaryDTO[]>([]);
  const [openLoading, setOpenLoading] = useState(false);
  const [openError, setOpenError] = useState<string | null>(null);
  const [generatingSignature, setGeneratingSignature] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    SourceFileService.listarPorProjeto(projectId)
      .then((list) => {
        if (!cancelled) setFiles(list);
      })
      .catch(() => {
        if (!cancelled) setLoadError('Erro ao carregar a lista de arquivos.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [projectId]);

  const handleOpen = async (file: GFCSourceFileDTO) => {
    setOpenedFile(file);
    setOpenLoading(true);
    setOpenError(null);
    try {
      const [methods, gfcs] = await Promise.all([
        SourceFileService.listarMetodos(projectId, file.id),
        GFCService.listarPorProjeto(projectId),
      ]);
      setOpenedMethods(methods);
      setProjectGfcs(gfcs);
    } catch {
      setOpenError('Erro ao carregar dados do arquivo.');
    } finally {
      setOpenLoading(false);
    }
  };

  const handleCloseViewer = () => {
    setOpenedFile(null);
    setOpenedMethods([]);
    setOpenError(null);
    setGeneratingSignature(null);
  };

  const handleGenerate = async (method: GFCSourceMethodDTO) => {
    if (!openedFile) return;
    setGeneratingSignature(method.signature);
    try {
      const { id } = await GFCService.criar(projectId, {
        projectId,
        sourceFileId: openedFile.id,
        methodSignature: method.signature,
        name: method.name,
      });
      navigate(`/projeto/${projectId}/gfc/${id}`);
    } catch {
      setOpenError('Erro ao gerar GFC para esse método.');
      setGeneratingSignature(null);
    }
  };

  const handleOpenGfc = (gfcId: string) => {
    navigate(`/projeto/${projectId}/gfc/${gfcId}`);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await SourceFileService.deletar(projectId, deleteTarget.id);
    setFiles((prev) => prev.filter((f) => f.id !== deleteTarget.id));
    setDeleteTarget(null);
  };

  return (
    <div className="container mx-auto px-6 py-6">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-base font-semibold text-gray-200">Arquivos de código</h2>
          <p className="text-sm text-gray-500 mt-0.5">
            {loading
              ? 'Carregando...'
              : `${files.length} arquivo${files.length !== 1 ? 's' : ''} .java`}
          </p>
        </div>
      </div>

      {loadError ? (
        <div className="bg-surface-card border border-edge rounded-lg p-6">
          <p className="text-sm text-red-400">{loadError}</p>
        </div>
      ) : !loading && files.length === 0 ? (
        <div className="bg-surface-card border border-edge rounded-lg p-12 flex flex-col items-center gap-3">
          <div className="w-12 h-12 rounded-lg bg-blue-500/10 flex items-center justify-center">
            <FileCode className="w-6 h-6 text-blue-400" />
          </div>
          <p className="text-sm text-gray-400">
            Nenhum arquivo enviado ainda. Crie um GFC para começar a subir arquivos.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {files.map((file) => (
            <div
              key={file.id}
              className="bg-surface-card border border-edge rounded-lg p-4 flex flex-col gap-3 hover:border-edge-hover transition-colors"
            >
              <div className="flex items-start justify-between">
                <div className="w-10 h-10 rounded bg-blue-500/10 border border-edge flex items-center justify-center text-blue-400 shrink-0">
                  <FileCode className="w-5 h-5" />
                </div>
                <button
                  onClick={() => setDeleteTarget(file)}
                  className="text-gray-600 hover:text-red-400 transition-colors p-1 -mr-1"
                  title="Excluir arquivo"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>

              <div className="flex-1 min-w-0">
                <h3 className="text-sm font-medium text-gray-200 truncate" title={file.fileName}>
                  {file.fileName}
                </h3>
                <p className="text-xs text-gray-500 mt-0.5">{file.language}</p>
                <p className="text-xs text-gray-600 mt-1">
                  Adicionado {formatRelativeDate(file.createdAt)}
                </p>
              </div>

              <Button
                size="sm"
                className="w-full justify-center"
                onClick={() => handleOpen(file)}
              >
                <Eye className="w-4 h-4" />
                Ver código
              </Button>
            </div>
          ))}
        </div>
      )}

      {openedFile && openLoading && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70">
          <p className="text-gray-300 text-sm">Carregando arquivo...</p>
        </div>
      )}

      {openedFile && !openLoading && !openError && (
        <SourceFileViewerModal
          sourceFile={openedFile}
          methods={openedMethods}
          existingGfcs={projectGfcs}
          generatingSignature={generatingSignature}
          onClose={handleCloseViewer}
          onGenerate={handleGenerate}
          onOpenGfc={handleOpenGfc}
        />
      )}

      {openError && (
        <div className="fixed bottom-4 right-4 bg-red-900 text-red-200 px-4 py-2 rounded">
          {openError}
        </div>
      )}

      {deleteTarget && (
        <ConfirmModal
          title="Excluir arquivo"
          message={
            <>
              Tem certeza que deseja excluir o arquivo{' '}
              <span className="text-gray-200 font-medium">{deleteTarget.fileName}</span>? Os GFCs
              vinculados a esse arquivo também serão removidos.
            </>
          }
          icon={AlertTriangle}
          confirmLabel="Excluir"
          confirmLoadingLabel="Excluindo..."
          onClose={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  );
}
