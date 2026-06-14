import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, Clock, Plus, Trash2 } from 'lucide-react';
import { Button } from '../../../components/Button';
import { ConfirmModal } from '../../../components/ConfirmModal';
import { CardGridSkeleton } from '../../../components/CardGridSkeleton';
import { ARTIFACT_TYPES } from '../../../shared/artifactTypes';
import GCEService from '../../../services/GCE/GCEService';
import { formatRelativeDate } from '../../../utils/formatDate';
import type { GCEDTO } from '../types/gce';

interface GCEListProps {
  projectId: string;
  onCreateGCE: () => void;
}

export function GCEList({ projectId, onCreateGCE }: GCEListProps) {
  const navigate = useNavigate();
  const [gces, setGces] = useState<GCEDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState<GCEDTO | null>(null);

  useEffect(() => {
    GCEService.listarPorProjeto(projectId)
      .then(setGces)
      .finally(() => setLoading(false));
  }, [projectId]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await GCEService.deletar(projectId, deleteTarget.id);
    setGces((prev) => prev.filter((g) => g.id !== deleteTarget.id));
    setDeleteTarget(null);
  };

  const typeConfig = ARTIFACT_TYPES['GCE'];
  const Icon = typeConfig.icon;

  return (
    <div className="container mx-auto px-6 py-6">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-base font-semibold text-gray-200">Grafos de Causa e Efeito</h2>
          <p className="text-sm text-gray-500 mt-0.5">
            {loading ? (
              <span className="inline-block h-3.5 w-24 bg-surface-hover rounded animate-pulse align-middle" />
            ) : (
              `${gces.length} grafo${gces.length !== 1 ? 's' : ''}`
            )}
          </p>
        </div>
        <Button size="sm" variant="primary" onClick={onCreateGCE}>
          <Plus className="w-4 h-4" />
          Novo GCE
        </Button>
      </div>

      {loading ? (
        <CardGridSkeleton />
      ) : gces.length === 0 ? (
        <div className="bg-surface-card border border-edge rounded-lg p-12 flex flex-col items-center gap-3">
          <div className={`w-12 h-12 rounded-lg ${typeConfig.bgColor} flex items-center justify-center`}>
            <Icon className={`w-6 h-6 ${typeConfig.color}`} />
          </div>
          <p className="text-sm text-gray-400">Nenhum GCE criado ainda.</p>
          <Button size="sm" variant="primary" onClick={onCreateGCE}>
            <Plus className="w-4 h-4" />
            Criar primeiro GCE
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {gces.map((gce) => (
            <div
              key={gce.id}
              className="bg-surface-card border border-edge rounded-lg p-4 flex flex-col gap-3 hover:border-edge-hover transition-colors"
            >
              <div className="flex items-start justify-between">
                <div className={`w-10 h-10 rounded ${typeConfig.bgColor} border border-edge flex items-center justify-center ${typeConfig.color} shrink-0`}>
                  <Icon className="w-5 h-5" />
                </div>
                <button
                  onClick={() => setDeleteTarget(gce)}
                  className="text-gray-600 hover:text-red-400 transition-colors p-1 -mr-1"
                  title="Excluir GCE"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>

              <div className="flex-1 min-w-0">
                <h3 className="text-sm font-medium text-gray-200 truncate" title={gce.name}>
                  {gce.name}
                </h3>
                {gce.description && (
                  <p className="text-xs text-gray-500 mt-0.5 truncate" title={gce.description}>
                    {gce.description}
                  </p>
                )}
                <p className="text-xs text-gray-600 mt-1">
                  {gce.nodes.length} nó{gce.nodes.length !== 1 ? 's' : ''} · {gce.edges.length} aresta{gce.edges.length !== 1 ? 's' : ''}
                </p>
                <div className="flex items-center gap-1 text-xs text-gray-600 mt-1">
                  <Clock className="w-3 h-3" />
                  {formatRelativeDate(gce.updatedAt ?? gce.createdAt)}
                </div>
              </div>

              <Button
                size="sm"
                className="w-full justify-center"
                onClick={() => navigate(`/projeto/${projectId}/gce/${gce.id}`)}
              >
                Abrir
              </Button>
            </div>
          ))}
        </div>
      )}

      {deleteTarget && (
        <ConfirmModal
          title="Excluir GCE"
          message={
            <>
              Tem certeza que deseja excluir o GCE{' '}
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
