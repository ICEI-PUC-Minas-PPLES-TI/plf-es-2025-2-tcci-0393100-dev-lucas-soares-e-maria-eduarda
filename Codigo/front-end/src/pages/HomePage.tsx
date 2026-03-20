import { useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/hooks/useAuth';

export function HomePage() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-[#020617] flex items-center justify-center">
      <div className="text-center space-y-4">
        <h1 className="text-white text-2xl">Home</h1>
        <button
          onClick={handleLogout}
          className="px-4 py-2 bg-red-600 hover:bg-red-500 text-white rounded-lg transition-colors"
        >
          Sair
        </button>
      </div>
    </div>
  );
}