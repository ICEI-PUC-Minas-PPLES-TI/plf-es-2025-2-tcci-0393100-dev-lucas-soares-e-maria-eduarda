interface AuthCardProps {
  children: React.ReactNode;
}

export function AuthCard({ children }: AuthCardProps) {
  return (
    <div className="bg-surface-card rounded-xl shadow-2xl p-8 border border-primary/10 hover:border-primary/20 transition-all duration-300 hover:shadow-primary/10">
      {children}
    </div>
  );
}