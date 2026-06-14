import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Header } from '../components/Header';
import { Footer } from '../components/Footer';
import { HeroSection } from '../features/home/components/HeroSection';
import { ProjectsSection } from '../features/home/components/ProjectsSection';
import { QuickActionsSection } from '../features/home/components/QuickActionsSection';
import { RecentArtifactsSection } from '../features/home/components/RecentArtifactsSection';
import { ProjectFormModal } from '../features/projects/components/ProjectFormModal';
import { GCEFormModal } from '../features/gce/components/GCEFormModal';
import { CreateGFCModal } from '../features/graph/components/CreateGFCModal';
import { SelectGCEForTableModal } from '../features/decision-table/components/SelectGCEForTableModal';

export function HomePage() {
  const navigate = useNavigate();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showCreateGCEModal, setShowCreateGCEModal] = useState(false);
  const [showCreateGFCModal, setShowCreateGFCModal] = useState(false);
  const [showSelectGCEModal, setShowSelectGCEModal] = useState(false);

  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <Header />

      <main className="flex-1">
        <HeroSection onCreateProject={() => setShowCreateModal(true)} />
        <QuickActionsSection
          onCreateGFC={() => setShowCreateGFCModal(true)}
          onCreateGCE={() => setShowCreateGCEModal(true)}
          onGenerateTable={() => setShowSelectGCEModal(true)}
        />
        <ProjectsSection />
        <RecentArtifactsSection />
      </main>

      <Footer />

      {showCreateModal && (
        <ProjectFormModal
          onClose={() => setShowCreateModal(false)}
          onSuccess={(created) => {
            setShowCreateModal(false);
            navigate(`/projeto/${created.id}`);
          }}
        />
      )}

      {showCreateGFCModal && (
        <CreateGFCModal
          onClose={() => setShowCreateGFCModal(false)}
        />
      )}

      {showCreateGCEModal && (
        <GCEFormModal
          onClose={() => setShowCreateGCEModal(false)}
        />
      )}

      {showSelectGCEModal && (
        <SelectGCEForTableModal
          onClose={() => setShowSelectGCEModal(false)}
        />
      )}
    </div>
  );
}
