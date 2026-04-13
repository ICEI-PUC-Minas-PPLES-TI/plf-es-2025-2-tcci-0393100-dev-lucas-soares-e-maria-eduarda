import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AlertTriangle } from 'lucide-react';
import { Header } from '../components/Header';
import { ConfirmModal } from '../components/ConfirmModal';
import { ProjectHeader } from '../features/projects/components/ProjectHeader';
import { ProjectTabs } from '../features/projects/components/ProjectTabs';
import { ProjectSummary } from '../features/projects/components/ProjectSummary';
import { QuickActions } from '../features/projects/components/QuickActions';
import { RecentArtifacts } from '../features/projects/components/RecentArtifacts';
import { ValidationWarnings } from '../features/projects/components/ValidationWarnings';
import { ProjectFormModal } from '../features/projects/components/ProjectFormModal';
import { GCEFormModal } from '../features/gce/components/GCEFormModal';
import { GCEList } from '../features/gce/components/GCEList';
import ProjectService from '../services/Project/ProjectService';
import type { ProjectDTO } from '../services/Project/types/project';

export function ProjectPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');
  const [project, setProject] = useState<ProjectDTO | null>(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showCreateGCEModal, setShowCreateGCEModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    let cancelled = false;

    ProjectService.buscarPorId(id)
      .then((data) => {
        if (!cancelled) setProject(data);
      })
      .catch(() => {
        if (!cancelled) setError('Erro ao carregar o projeto.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => { cancelled = true; };
  }, [id]);

  const handleDelete = async () => {
    if (!id) return;
    try {
      await ProjectService.excluir(id);
      navigate('/homepage');
    } catch {
      setError('Erro ao excluir o projeto.');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-gray-400">Carregando projeto...</p>
      </div>
    );
  }

  if (error || !project) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-red-400">{error ?? 'Projeto não encontrado.'}</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <Header
        breadcrumb={[
          { label: 'Projetos', href: '/homepage' },
          { label: project.name },
        ]}
      />
      <ProjectHeader
        projectName={project.name}
        projectDescription={project.description}
        onEdit={() => setShowEditModal(true)}
        onDelete={() => setShowDeleteModal(true)}
      />
      <ProjectTabs activeTab={activeTab} onTabChange={setActiveTab} />

      <main className="flex-1">
        {activeTab === 'overview' && (
          <div className="container mx-auto px-6 py-6 flex gap-6">
            <div className="flex-1 space-y-6 min-w-0">
              <QuickActions onCreateGCE={() => setShowCreateGCEModal(true)} />
              <RecentArtifacts projectId={project.id} />
              <ValidationWarnings />
            </div>
            <aside className="w-80 shrink-0 hidden lg:block">
              <ProjectSummary />
            </aside>
          </div>
        )}

        {activeTab === 'gce' && (
          <GCEList
            projectId={project.id}
            onCreateGCE={() => setShowCreateGCEModal(true)}
          />
        )}
      </main>

      {showDeleteModal && (
        <ConfirmModal
          title="Excluir projeto"
          message={
            <>
              Tem certeza que deseja excluir o projeto <span className="text-gray-200 font-medium">{project.name}</span>? Essa ação não pode ser desfeita.
            </>
          }
          icon={AlertTriangle}
          confirmLabel="Excluir"
          confirmLoadingLabel="Excluindo..."
          onClose={() => setShowDeleteModal(false)}
          onConfirm={handleDelete}
        />
      )}

      {showCreateGCEModal && (
        <GCEFormModal
          projectId={project.id}
          onClose={() => setShowCreateGCEModal(false)}
        />
      )}

      {showEditModal && (
        <ProjectFormModal
          project={project}
          onClose={() => setShowEditModal(false)}
          onSuccess={(updated) => {
            setProject(updated);
            setShowEditModal(false);
          }}
        />
      )}
    </div>
  );
}
