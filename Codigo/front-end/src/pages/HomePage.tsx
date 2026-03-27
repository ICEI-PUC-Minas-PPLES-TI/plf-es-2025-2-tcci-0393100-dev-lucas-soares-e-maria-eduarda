import { Header } from '../components/Header';
import { Footer } from '../components/Footer';
import { HeroSection } from '../features/home/components/HeroSection';
import { ProjectsSection } from '../features/home/components/ProjectsSection';
import { QuickActionsSection } from '../features/home/components/QuickActionsSection';
import { RecentArtifactsSection } from '../features/home/components/RecentArtifactsSection';

export function HomePage() {
  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <Header />

      <main className="flex-1">
        <HeroSection />
        <ProjectsSection />
        <QuickActionsSection />
        <RecentArtifactsSection />
      </main>

      <Footer />
    </div>
  );
}
