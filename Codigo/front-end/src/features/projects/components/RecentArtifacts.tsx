import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Clock, ExternalLink, FileCode, Network } from 'lucide-react';
import { Button } from '../../../components/Button';
import { SectionHeader } from '../../../components/SectionHeader';
import { ARTIFACT_TYPES, type ArtifactType } from '../../../shared/artifactTypes';
import ProjectService from '../../../services/Project/ProjectService';
import { formatRelativeDate } from '../../../utils/formatDate';
import type { ProjectArtifactDTO, ProjectArtifactType } from '../../../services/Project/types/project';

interface RecentArtifactsProps {
  projectId: string;
}

const MAX_ITEMS = 5;

const BACKEND_TO_CONFIG: Record<ProjectArtifactType, ArtifactType> = {
  GCE: 'GCE',
  GFC: 'GFC',
  DECISION_TABLE: 'TABLE',
};

export function RecentArtifacts({ projectId }: RecentArtifactsProps) {
  const navigate = useNavigate();
  const [artifacts, setArtifacts] = useState<ProjectArtifactDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    ProjectService.listarArtefatos(projectId)
      .then((list) => {
        if (!cancelled) setArtifacts(list);
      })
      .catch(() => {
        if (!cancelled) setArtifacts([]);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [projectId]);

  const recent = useMemo(() => artifacts.slice(0, MAX_ITEMS), [artifacts]);

  const handleOpen = (artifact: ProjectArtifactDTO) => {
    switch (artifact.type) {
      case 'GCE':
        navigate(`/projeto/${projectId}/gce/${artifact.id}`);
        return;
      case 'GFC':
        navigate(`/projeto/${projectId}/gfc/${artifact.id}`);
        return;
      case 'DECISION_TABLE':
        if (artifact.relatedArtifact?.type === 'GCE') {
          navigate(`/projeto/${projectId}/gce/${artifact.relatedArtifact.id}/tabela-decisao`);
        }
        return;
    }
  };

  if (loading) {
    return (
      <div className="bg-surface-card border border-edge rounded-lg p-5">
        <SectionHeader title="Artefatos Recentes" />
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-3">
          {[0, 1, 2, 3].map((i) => (
            <div
              key={i}
              className="bg-surface border border-edge rounded-lg p-4 h-44 animate-pulse"
              style={{ animationDelay: `${i * 80}ms` }}
            />
          ))}
        </div>
      </div>
    );
  }

  if (recent.length === 0) {
    return (
      <div className="bg-surface-card border border-edge rounded-lg p-5">
        <SectionHeader title="Artefatos Recentes" />
        <p className="text-sm text-gray-500 text-center py-6">
          Nenhum artefato ainda. Comece criando um GCE, GFC ou Tabela de Decisão.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader
        title="Artefatos Recentes"
        action={
          artifacts.length > MAX_ITEMS ? (
            <span className="text-xs text-gray-500">
              {recent.length} de {artifacts.length}
            </span>
          ) : (
            <span className="text-xs text-gray-500">
              {artifacts.length} {artifacts.length === 1 ? 'item' : 'itens'}
            </span>
          )
        }
      />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-3">
        {recent.map((artifact) => {
          const typeConfig = ARTIFACT_TYPES[BACKEND_TO_CONFIG[artifact.type]];
          const Icon = typeConfig.icon;
          const referenceDate = artifact.updatedAt ?? artifact.createdAt;

          return (
            <button
              key={`${artifact.type}-${artifact.id}`}
              onClick={() => handleOpen(artifact)}
              className="bg-surface border border-edge rounded-lg p-4 hover:border-edge-hover transition-colors cursor-pointer group flex flex-col text-left"
            >
              <div className="flex items-start justify-between mb-3">
                <div className={`w-10 h-10 rounded ${typeConfig.bgColor} border border-edge flex items-center justify-center ${typeConfig.color}`}>
                  <Icon className="w-5 h-5" />
                </div>
                <span className="opacity-0 group-hover:opacity-100 transition-opacity text-gray-400 group-hover:text-primary-light">
                  <ExternalLink className="w-4 h-4" />
                </span>
              </div>

              <h3 className="text-sm mb-1 truncate" title={artifact.name}>
                {artifact.name}
              </h3>
              <p className="text-xs text-gray-500 mb-2">{typeConfig.label}</p>

              {artifact.relatedArtifact?.name && (() => {
                const isGce = artifact.relatedArtifact.type === 'GCE';
                const RelatedIcon = isGce ? Network : FileCode;
                const iconColor = isGce ? 'text-green-400' : 'text-blue-400';
                return (
                  <p
                    className="text-xs text-gray-500 mb-2 flex items-center gap-1 truncate"
                    title={artifact.relatedArtifact.name}
                  >
                    <RelatedIcon className={`w-3 h-3 shrink-0 ${iconColor}`} />
                    <span className={`truncate ${isGce ? '' : 'font-mono'}`}>
                      {artifact.relatedArtifact.name}
                    </span>
                  </p>
                );
              })()}

              <div className="flex items-center gap-1 text-xs text-gray-500 mb-3 mt-auto">
                <Clock className="w-3 h-3" />
                {formatRelativeDate(referenceDate)}
              </div>

              <Button
                size="sm"
                className="w-full justify-center"
                onClick={(e) => {
                  e.stopPropagation();
                  handleOpen(artifact);
                }}
              >
                Abrir
              </Button>
            </button>
          );
        })}
      </div>
    </div>
  );
}
