import { useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
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
import { GFCList } from '../features/graph/components/GFCList';
import { CreateGFCModal } from '../features/graph/components/CreateGFCModal';
import { SourceFileList } from '../features/graph/components/SourceFileList';
import { DecisionTableList } from '../features/decision-table/components/DecisionTableList';
import { SelectGCEForTableModal } from '../features/decision-table/components/SelectGCEForTableModal';
import ProjectService from '../services/Project/ProjectService';
import type { ProjectLayoutContext } from './ProjectLayout';

export function ProjectPage() {
  const { project, setProject } = useOutletContext<ProjectLayoutContext>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showCreateGCEModal, setShowCreateGCEModal] = useState(false);
  const [showCreateGFCModal, setShowCreateGFCModal] = useState(false);
  const [showSelectGCEModal, setShowSelectGCEModal] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const handleDelete = async () => {
    try {
      await ProjectService.excluir(project.id);
      navigate('/homepage');
    } catch {
      setDeleteError('Erro ao excluir o projeto.');
    }
  };

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
        createdAt={project.createdAt}
        updatedAt={project.updatedAt}
        onEdit={() => setShowEditModal(true)}
        onDelete={() => setShowDeleteModal(true)}
      />
      <ProjectTabs activeTab={activeTab} onTabChange={setActiveTab} />

      <main className="flex-1">
        {activeTab === 'overview' && (
          <div className="container mx-auto px-6 py-6 flex gap-6">
            <div className="flex-1 space-y-6 min-w-0">
              <QuickActions
                onCreateGCE={() => setShowCreateGCEModal(true)}
                onGenerateTable={() => setShowSelectGCEModal(true)}
                onCreateGFC={() => setShowCreateGFCModal(true)}
              />
              <RecentArtifacts projectId={project.id} />
              <ValidationWarnings projectId={project.id} />
            </div>
            <aside className="w-80 shrink-0 hidden lg:block">
              <ProjectSummary createdAt={project.createdAt} updatedAt={project.updatedAt} />
            </aside>
          </div>
        )}

        {activeTab === 'artifacts' && (
          <SourceFileList projectId={project.id} />
        )}

        {activeTab === 'gfc' && (
          <GFCList
            projectId={project.id}
            onCreateGFC={() => setShowCreateGFCModal(true)}
          />
        )}

        {activeTab === 'gce' && (
          <GCEList
            projectId={project.id}
            onCreateGCE={() => setShowCreateGCEModal(true)}
          />
        )}

        {activeTab === 'tables' && (
          <DecisionTableList projectId={project.id} />
        )}
      </main>

      {deleteError && (
        <div className="fixed bottom-4 right-4 bg-red-900 text-red-200 px-4 py-2 rounded">
          {deleteError}
        </div>
      )}

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

      {showCreateGFCModal && (
        <CreateGFCModal
          projectId={project.id}
          onClose={() => setShowCreateGFCModal(false)}
        />
      )}

      {showSelectGCEModal && (
        <SelectGCEForTableModal
          projectId={project.id}
          onClose={() => setShowSelectGCEModal(false)}
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
