import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ExternalLink } from 'lucide-react';
import { motion } from 'motion/react';
import { Button } from '../../../components/Button';
import { ARTIFACT_TYPES, type ArtifactType } from '../../../shared/artifactTypes';
import ProjectService from '../../../services/Project/ProjectService';
import type { ProjectArtifactDTO, ProjectArtifactType } from '../../../services/Project/types/project';

const MAX_ITEMS = 5;

const BACKEND_TO_CONFIG: Record<ProjectArtifactType, ArtifactType> = {
  GCE: 'GCE',
  GFC: 'GFC',
  DECISION_TABLE: 'TABLE',
};

type ArtifactWithProject = ProjectArtifactDTO & { projectId: string };

function referenceDate(artifact: ProjectArtifactDTO): number {
  return new Date(artifact.updatedAt ?? artifact.createdAt).getTime();
}

export function RecentArtifactsSection() {
  const navigate = useNavigate();
  const [artifacts, setArtifacts] = useState<ArtifactWithProject[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const projects = await ProjectService.listarMeus();
        const results = await Promise.allSettled(
          projects.map((p) => ProjectService.listarArtefatos(p.id)),
        );

        const merged: ArtifactWithProject[] = [];
        results.forEach((result, i) => {
          if (result.status === 'fulfilled') {
            result.value.forEach((artifact) =>
              merged.push({ ...artifact, projectId: projects[i].id }),
            );
          }
        });

        merged.sort((a, b) => referenceDate(b) - referenceDate(a));
        if (!cancelled) setArtifacts(merged.slice(0, MAX_ITEMS));
      } catch {
        if (!cancelled) setArtifacts([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => { cancelled = true; };
  }, []);

  const isEmpty = useMemo(() => !loading && artifacts.length === 0, [loading, artifacts]);

  const handleOpen = (artifact: ArtifactWithProject) => {
    switch (artifact.type) {
      case 'GCE':
        navigate(`/projeto/${artifact.projectId}/gce/${artifact.id}`);
        return;
      case 'GFC':
        navigate(`/projeto/${artifact.projectId}/gfc/${artifact.id}`);
        return;
      case 'DECISION_TABLE':
        if (artifact.relatedArtifact?.type === 'GCE') {
          navigate(`/projeto/${artifact.projectId}/gce/${artifact.relatedArtifact.id}/tabela-decisao`);
        }
        return;
    }
  };

  return (
    <section className="container mx-auto px-6 py-16 border-t border-edge">
      <h3 className="text-gray-100 text-xl font-semibold mb-8">Últimos Artefatos Modificados</h3>

      <div className="bg-surface-elevated border border-edge rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-surface border-b border-edge">
              <tr>
                <th className="px-6 py-3 text-left text-xs text-gray-400 uppercase tracking-wider">
                  Nome
                </th>
                <th className="px-6 py-3 text-left text-xs text-gray-400 uppercase tracking-wider">
                  Tipo
                </th>
                <th className="px-6 py-3 text-left text-xs text-gray-400 uppercase tracking-wider">
                  Última Modificação
                </th>
                <th className="px-6 py-3 text-right text-xs text-gray-400 uppercase tracking-wider">
                  Ação
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-edge">
              {loading &&
                [0, 1, 2, 3, 4].map((i) => (
                  <tr key={i}>
                    <td className="px-6 py-4" colSpan={4}>
                      <div className="h-5 bg-surface rounded animate-pulse" style={{ animationDelay: `${i * 80}ms` }} />
                    </td>
                  </tr>
                ))}

              {isEmpty && (
                <tr>
                  <td className="px-6 py-10 text-center text-sm text-gray-500" colSpan={4}>
                    Nenhum artefato ainda. Comece criando um GCE, GFC ou Tabela de Decisão.
                  </td>
                </tr>
              )}

              {!loading &&
                artifacts.map((artifact, index) => {
                  const typeConfig = ARTIFACT_TYPES[BACKEND_TO_CONFIG[artifact.type]];
                  const Icon = typeConfig.icon;
                  const date = artifact.updatedAt ?? artifact.createdAt;

                  return (
                    <motion.tr
                      key={`${artifact.type}-${artifact.id}`}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ duration: 0.3, delay: index * 0.05 }}
                      className="hover:bg-surface transition-colors"
                    >
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center gap-3">
                          <Icon className="w-4 h-4 text-gray-400" />
                          <span className="text-sm text-gray-100">{artifact.name}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-3 py-1 rounded-full text-xs font-mono ${typeConfig.bgColor} ${typeConfig.color}`}>
                          {typeConfig.shortLabel}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-400">
                        {new Date(date).toLocaleDateString('pt-BR')}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right">
                        <Button variant="accent" className="inline-flex text-sm" onClick={() => handleOpen(artifact)}>
                          <ExternalLink className="w-4 h-4" />
                          Abrir
                        </Button>
                      </td>
                    </motion.tr>
                  );
                })}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
