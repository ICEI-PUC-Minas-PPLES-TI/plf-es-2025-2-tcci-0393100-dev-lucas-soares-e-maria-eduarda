import { useState, useEffect } from 'react';
import { useParams, Outlet } from 'react-router-dom';
import ProjectService from '../services/Project/ProjectService';
import { ProjectPageSkeleton } from '../features/projects/components/ProjectPageSkeleton';
import type { ProjectDTO } from '../services/Project/types/project';

export type ProjectLayoutContext = {
  project: ProjectDTO;
  setProject: (p: ProjectDTO) => void;
};

export function ProjectLayout() {
  const { projectId } = useParams<{ projectId: string }>();
  const [project, setProject] = useState<ProjectDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!projectId) return;

    let cancelled = false;

    ProjectService.buscarPorId(projectId)
      .then((data) => { if (!cancelled) setProject(data); })
      .catch(() => { if (!cancelled) setError('Erro ao carregar o projeto.'); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [projectId]);

  if (loading) {
    return <ProjectPageSkeleton />;
  }

  if (error || !project) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-red-400">{error ?? 'Projeto não encontrado.'}</p>
      </div>
    );
  }

  return <Outlet context={{ project, setProject } as ProjectLayoutContext} />;
}
