import { User, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/hooks/useAuth';

export function Header() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-surface border-b border-gray-800">
      <div className="container mx-auto px-6 h-14 flex items-center justify-between">
        <div className="flex items-center gap-8">
          <h1 className="text-primary-light font-mono text-lg">GraphTest</h1>

          <nav className="hidden md:flex items-center gap-6">
            <a href="#projetos" className="text-gray-300 hover:text-primary-light transition-colors text-sm">
              Projetos
            </a>
            <a href="#gfc" className="text-gray-300 hover:text-primary-light transition-colors text-sm">
              GFC
            </a>
            <a href="#gce" className="text-gray-300 hover:text-primary-light transition-colors text-sm">
              GCE
            </a>
          </nav>
        </div>

        <div className="flex items-center gap-3">
          <button className="p-2 hover:bg-gray-800 rounded-lg transition-colors">
            <User className="w-5 h-5 text-gray-300" />
          </button>
          <button
            onClick={handleLogout}
            className="p-2 hover:bg-gray-800 rounded-lg transition-colors"
            title="Sair"
          >
            <LogOut className="w-5 h-5 text-gray-300" />
          </button>
        </div>
      </div>
    </header>
  );
}
