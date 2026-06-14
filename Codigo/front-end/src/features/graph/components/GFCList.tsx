import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, Clock, FileCode, Plus, Trash2 } from 'lucide-react';
import { Button } from '../../../components/Button';
import { ConfirmModal } from '../../../components/ConfirmModal';
import { CardGridSkeleton } from '../../../components/CardGridSkeleton';
import { ARTIFACT_TYPES } from '../../../shared/artifactTypes';
import GFCService from '../../../services/GFC/GFCService';
import SourceFileService from '../../../services/GFC/SourceFileService';
import { formatRelativeDate } from '../../../utils/formatDate';
import type { GFCSourceFileDTO, GFCSummaryDTO } from '../types/gfc';

interface GFCListProps {
  projectId: string;
  onCreateGFC: () => void;
}

export function GFCList({ projectId, onCreateGFC }: GFCListProps) {
  const navigate = useNavigate();
  const [gfcs, setGfcs] = useState<GFCSummaryDTO[]>([]);
  const [sourceFiles, setSourceFiles] = useState<GFCSourceFileDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState<GFCSummaryDTO | null>(null);

  useEffect(() => {
    let cancelled = false;
    Promise.all([
      GFCService.listarPorProjeto(projectId),
      SourceFileService.listarPorProjeto(projectId),
    ])
      .then(([gfcList, files]) => {
        if (cancelled) return;
        setGfcs(gfcList);
        setSourceFiles(files);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [projectId]);

  const fileNameById = useMemo(() => {
    const map = new Map<string, string>();
    sourceFiles.forEach((f) => map.set(f.id, f.fileName));
    return map;
  }, [sourceFiles]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await GFCService.deletar(projectId, deleteTarget.id);
    setGfcs((prev) => prev.filter((g) => g.id !== deleteTarget.id));
    setDeleteTarget(null);
  };

  const typeConfig = ARTIFACT_TYPES['GFC'];
  const Icon = typeConfig.icon;

  return (
    <div className="container mx-auto px-6 py-6">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-base font-semibold text-gray-200">Grafos de Fluxo de Controle</h2>
          <p className="text-sm text-gray-500 mt-0.5">
            {loading ? (
              <span className="inline-block h-3.5 w-24 bg-surface-hover rounded animate-pulse align-middle" />
            ) : (
              `${gfcs.length} grafo${gfcs.length !== 1 ? 's' : ''}`
            )}
          </p>
        </div>
        <Button size="sm" variant="primary" onClick={onCreateGFC}>
          <Plus className="w-4 h-4" />
          Novo GFC
        </Button>
      </div>

      {loading ? (
        <CardGridSkeleton />
      ) : gfcs.length === 0 ? (
        <div className="bg-surface-card border border-edge rounded-lg p-12 flex flex-col items-center gap-3">
          <div className={`w-12 h-12 rounded-lg ${typeConfig.bgColor} flex items-center justify-center`}>
            <Icon className={`w-6 h-6 ${typeConfig.color}`} />
          </div>
          <p className="text-sm text-gray-400">Nenhum GFC criado ainda.</p>
          <Button size="sm" variant="primary" onClick={onCreateGFC}>
            <Plus className="w-4 h-4" />
            Criar primeiro GFC
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {gfcs.map((gfc) => (
            <div
              key={gfc.id}
              className="bg-surface-card border border-edge rounded-lg p-4 flex flex-col gap-3 hover:border-edge-hover transition-colors"
            >
              <div className="flex items-start justify-between">
                <div className={`w-10 h-10 rounded ${typeConfig.bgColor} border border-edge flex items-center justify-center ${typeConfig.color} shrink-0`}>
                  <Icon className="w-5 h-5" />
                </div>
                <button
                  onClick={() => setDeleteTarget(gfc)}
                  className="text-gray-600 hover:text-red-400 transition-colors p-1 -mr-1"
                  title="Excluir GFC"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>

              <div className="flex-1 min-w-0">
                <h3 className="text-sm font-medium text-gray-200 truncate" title={gfc.name}>
                  {gfc.name}
                </h3>
                <p className="text-xs text-gray-500 mt-0.5 font-mono truncate" title={gfc.methodSignature}>
                  {gfc.methodSignature}
                </p>
                {fileNameById.get(gfc.sourceFileId) && (
                  <p
                    className="text-xs text-gray-500 mt-1.5 flex items-center gap-1 truncate font-mono"
                    title={fileNameById.get(gfc.sourceFileId)}
                  >
                    <FileCode className="w-3 h-3 shrink-0 text-blue-400" />
                    <span className="truncate">{fileNameById.get(gfc.sourceFileId)}</span>
                  </p>
                )}
                {gfc.language && (
                  <p className="text-xs text-gray-600 mt-1">{gfc.language}</p>
                )}
                <p
                  className="text-xs text-gray-500 mt-1.5 flex items-center gap-1"
                  title={new Date(gfc.createdAt).toLocaleString('pt-BR')}
                >
                  <Clock className="w-3 h-3 shrink-0" />
                  <span className="truncate">Criado {formatRelativeDate(gfc.createdAt)}</span>
                </p>
              </div>

              <Button
                size="sm"
                className="w-full justify-center"
                onClick={() => navigate(`/projeto/${projectId}/gfc/${gfc.id}`)}
              >
                Abrir
              </Button>
            </div>
          ))}
        </div>
      )}

      {deleteTarget && (
        <ConfirmModal
          title="Excluir GFC"
          message={
            <>
              Tem certeza que deseja excluir o GFC{' '}
              <span className="text-gray-200 font-medium">{deleteTarget.name}</span>? Essa ação não pode ser desfeita.
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
