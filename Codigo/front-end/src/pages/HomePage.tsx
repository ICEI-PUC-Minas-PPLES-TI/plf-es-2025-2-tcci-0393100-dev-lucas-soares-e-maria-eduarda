import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Header } from '../components/Header';
import { Footer } from '../components/Footer';
import { HeroSection } from '../features/home/components/HeroSection';
import { ProjectsSection } from '../features/home/components/ProjectsSection';
import { QuickActionsSection } from '../features/home/components/QuickActionsSection';
import { RecentArtifactsSection } from '../features/home/components/RecentArtifactsSection';
import { CreateProjectModal } from '../features/projects/components/CreateProjectModal';

export function HomePage() {
  const navigate = useNavigate();
  const [showCreateModal, setShowCreateModal] = useState(false);

  const handleProjectCreated = (id: string) => {
    setShowCreateModal(false);
    navigate(`/projeto/${id}`);
  };

  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <Header />

      <main className="flex-1">
        <HeroSection onCreateProject={() => setShowCreateModal(true)} />
        <ProjectsSection />
        <QuickActionsSection />
        <RecentArtifactsSection />
      </main>

      <Footer />

      {showCreateModal && (
        <CreateProjectModal
          onClose={() => setShowCreateModal(false)}
          onCreated={handleProjectCreated}
        />
      )}
    </div>
  );
}
