import { useState } from 'react';
import { Header } from '../components/Header';
import { ProjectHeader } from '../features/projects/components/ProjectHeader';
import { ProjectTabs } from '../features/projects/components/ProjectTabs';
import { ProjectSummary } from '../features/projects/components/ProjectSummary';
import { QuickActions } from '../features/projects/components/QuickActions';
import { RecentArtifacts } from '../features/projects/components/RecentArtifacts';
import { ValidationWarnings } from '../features/projects/components/ValidationWarnings';

const mockProject = {
  name: 'Sistema de Login',
  description: 'Análise estrutural e funcional do módulo de autenticação',
};

export function ProjectPage() {
  const [activeTab, setActiveTab] = useState('overview');

  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <Header
        breadcrumb={[
          { label: 'Projetos', href: '/homepage' },
          { label: mockProject.name },
        ]}
      />
      <ProjectHeader
        projectName={mockProject.name}
        projectDescription={mockProject.description}
      />
      <ProjectTabs activeTab={activeTab} onTabChange={setActiveTab} />

      <main className="flex-1">
        {activeTab === 'overview' && (
          <div className="container mx-auto px-6 py-6 flex gap-6">
            <div className="flex-1 space-y-6 min-w-0">
              <QuickActions />
              <RecentArtifacts />
              <ValidationWarnings />
            </div>
            <aside className="w-80 shrink-0 hidden lg:block">
              <ProjectSummary />
            </aside>
          </div>
        )}
      </main>
    </div>
  );
}
