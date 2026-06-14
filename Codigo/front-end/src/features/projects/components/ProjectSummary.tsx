import { useEffect, useState } from 'react';
import { TrendingUp } from 'lucide-react';
import { SectionHeader } from '../../../components/SectionHeader';
import { ARTIFACT_TYPES } from '../../../shared/artifactTypes';
import { formatRelativeDate, formatShortDate } from '../../../utils/formatDate';
import ProjectService from '../../../services/Project/ProjectService';
import SourceFileService from '../../../services/GFC/SourceFileService';
import type {
  ProjectArtifactDTO,
  ProjectArtifactType,
} from '../../../services/Project/types/project';

interface ProjectSummaryProps {
  projectId: string;
  createdAt: string;
  updatedAt: string | null;
}

interface Counts {
  files: number;
  byType: Record<ProjectArtifactType, number>;
}

const EMPTY_COUNTS: Counts = {
  files: 0,
  byType: { GFC: 0, GCE: 0, DECISION_TABLE: 0 },
};

function tally(artifacts: ProjectArtifactDTO[], fileCount: number): Counts {
  const byType: Record<ProjectArtifactType, number> = { GFC: 0, GCE: 0, DECISION_TABLE: 0 };
  artifacts.forEach((a) => {
    byType[a.type] += 1;
  });
  return { files: fileCount, byType };
}

export function ProjectSummary({ projectId, createdAt, updatedAt }: ProjectSummaryProps) {
  const [counts, setCounts] = useState<Counts>(EMPTY_COUNTS);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    Promise.all([
      ProjectService.listarArtefatos(projectId),
      SourceFileService.listarPorProjeto(projectId),
    ])
      .then(([artifacts, files]) => {
        if (!cancelled) setCounts(tally(artifacts, files.length));
      })
      .catch(() => {
        if (!cancelled) setCounts(EMPTY_COUNTS);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [projectId]);

  const stats = [
    {
      label: 'Arquivos',
      value: counts.files,
      icon: ARTIFACT_TYPES.CODE.icon,
      color: ARTIFACT_TYPES.CODE.color,
    },
    {
      label: 'GFCs',
      value: counts.byType.GFC,
      icon: ARTIFACT_TYPES.GFC.icon,
      color: ARTIFACT_TYPES.GFC.color,
    },
    {
      label: 'GCEs',
      value: counts.byType.GCE,
      icon: ARTIFACT_TYPES.GCE.icon,
      color: ARTIFACT_TYPES.GCE.color,
    },
    {
      label: 'Tabelas de Decisão',
      value: counts.byType.DECISION_TABLE,
      icon: ARTIFACT_TYPES.TABLE.icon,
      color: ARTIFACT_TYPES.TABLE.color,
    },
  ];

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader title="Resumo do Projeto" icon={TrendingUp} />

      <div className="space-y-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div
                  className={`w-10 h-10 rounded bg-surface border border-edge flex items-center justify-center ${stat.color}`}
                >
                  <Icon className="w-5 h-5" />
                </div>
                <span className="text-sm text-gray-300">{stat.label}</span>
              </div>
              {loading ? (
                <span className="inline-block w-6 h-6 bg-surface-hover rounded animate-pulse" />
              ) : (
                <span className="text-2xl tabular-nums">{stat.value}</span>
              )}
            </div>
          );
        })}
      </div>

      <div className="mt-6 pt-4 border-t border-edge"></div>

      <div className="mt-4 grid grid-cols-2 gap-3">
        <div className="bg-surface border border-edge rounded p-3">
          <p className="text-xs text-gray-500 mb-1">Criado em</p>
          <p className="text-sm">{formatShortDate(createdAt)}</p>
        </div>
        <div className="bg-surface border border-edge rounded p-3">
          <p className="text-xs text-gray-500 mb-1">Última atualização</p>
          <p className="text-sm">
            {updatedAt ? formatRelativeDate(updatedAt) : '—'}
          </p>
        </div>
      </div>
    </div>
  );
}
