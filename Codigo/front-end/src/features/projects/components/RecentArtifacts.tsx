import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ExternalLink, Clock } from 'lucide-react';
import { Button } from '../../../components/Button';
import { SectionHeader } from '../../../components/SectionHeader';
import { ARTIFACT_TYPES } from '../../../shared/artifactTypes';
import GCEService from '../../../services/GCE/GCEService';
import type { GCEDTO } from '../../gce/types/gce';

interface RecentArtifactsProps {
  projectId: string;
}

export function RecentArtifacts({ projectId }: RecentArtifactsProps) {
  const navigate = useNavigate();
  const [gces, setGces] = useState<GCEDTO[]>([]);

  useEffect(() => {
    GCEService.listarPorProjeto(projectId).then(setGces);
  }, [projectId]);

  const typeConfig = ARTIFACT_TYPES['GCE'];
  const Icon = typeConfig.icon;

  if (gces.length === 0) {
    return (
      <div className="bg-surface-card border border-edge rounded-lg p-5">
        <SectionHeader title="Artefatos Recentes" />
        <p className="text-sm text-gray-500 text-center py-6">
          Nenhum artefato ainda. Comece criando um GCE.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader title="Artefatos Recentes" />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-3">
        {gces.map((gce) => (
          <div
            key={gce.id}
            className="bg-surface border border-edge rounded-lg p-4 hover:border-edge-hover transition-colors cursor-pointer group"
          >
            <div className="flex items-start justify-between mb-3">
              <div className={`w-10 h-10 rounded ${typeConfig.bgColor} border border-edge flex items-center justify-center ${typeConfig.color}`}>
                <Icon className="w-5 h-5" />
              </div>
              <button className="opacity-0 group-hover:opacity-100 transition-opacity text-gray-400 hover:text-primary-light">
                <ExternalLink className="w-4 h-4" />
              </button>
            </div>

            <h3 className="text-sm mb-1 truncate" title={gce.name}>
              {gce.name}
            </h3>
            <p className="text-xs text-gray-500 mb-2">{typeConfig.label}</p>
            {gce.description && (
              <p className="text-xs text-gray-600 mb-3 truncate" title={gce.description}>
                {gce.description}
              </p>
            )}

            <div className="flex items-center gap-1 text-xs text-gray-500 mb-3">
              <Clock className="w-3 h-3" />
              {gce.nodes.length} nó{gce.nodes.length !== 1 ? 's' : ''}
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
    </div>
  );
}
