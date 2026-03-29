import { User, LogOut } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/hooks/useAuth';
import { Breadcrumb } from './Breadcrumb';

interface BreadcrumbItem {
  label: string;
  href?: string;
}

interface HeaderProps {
  breadcrumb?: BreadcrumbItem[];
}

export function Header({ breadcrumb }: HeaderProps) {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-surface border-b border-edge">
      <div className="container mx-auto px-6 h-14 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link to="/homepage" className="text-primary-light font-mono text-lg hover:text-cyan-300 transition-colors">
            GraphTest
          </Link>
          {breadcrumb && <Breadcrumb items={breadcrumb} />}
        </div>

        <div className="flex items-center gap-3">
          <button className="p-2 hover:bg-surface-hover rounded-lg transition-colors">
            <User className="w-5 h-5 text-gray-300" />
          </button>
          <button
            onClick={handleLogout}
            className="p-2 hover:bg-surface-hover rounded-lg transition-colors"
            title="Sair"
          >
            <LogOut className="w-5 h-5 text-gray-300" />
          </button>
        </div>
      </div>
    </header>
  );
}
