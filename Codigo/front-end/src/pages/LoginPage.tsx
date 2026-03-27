import { GraphBackground } from '../components/GraphBackground';
import { LoginCard } from '../features/auth/components/LoginCard';

export function LoginPage() {
  return (
    <div className="relative min-h-screen flex items-center justify-center">
      <GraphBackground />
      <LoginCard />
    </div>
  );
}