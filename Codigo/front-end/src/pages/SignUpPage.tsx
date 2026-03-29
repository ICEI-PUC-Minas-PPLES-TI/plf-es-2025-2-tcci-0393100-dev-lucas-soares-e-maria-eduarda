import { GraphBackground } from '../components/GraphBackground';
import { SignUpCard } from '../features/auth/components/SignUpCard';

export function SignUpPage() {
  return (
    <div className="relative min-h-screen flex items-center justify-center">
      <GraphBackground />
      <div className="relative z-10">
        <SignUpCard />
      </div>
    </div>
  );
}