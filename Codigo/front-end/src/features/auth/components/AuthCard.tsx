interface AuthCardProps {
  children: React.ReactNode;
}

export function AuthCard({ children }: AuthCardProps) {
  return (
    <div className="bg-[#0f1729] rounded-xl shadow-2xl p-8 border border-cyan-500/10 hover:border-cyan-500/20 transition-all duration-300 hover:shadow-cyan-500/10">
      {children}
    </div>
  );
}