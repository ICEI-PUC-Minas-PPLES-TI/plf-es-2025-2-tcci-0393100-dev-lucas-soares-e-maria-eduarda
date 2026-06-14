import { useState, type SyntheticEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthCard } from './AuthCard';
import { FormField } from './FormField';
import { PasswordField } from './PasswordField';
import { Button } from '../../../components/Button';
import { useAuth } from '../hooks/useAuth';

export function LoginCard() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await login(formData.email, formData.password);
      navigate('/');
    } catch {
      setError('Email ou senha inválidos.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative z-10 w-full max-w-105 px-4">
      <AuthCard>
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-semibold text-white mb-2">
            Acessar sua conta
          </h1>
          <p className="text-gray-400 text-sm">
            Entre para utilizar o GraphTest
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <FormField
            id="email"
            label="Email"
            type="email"
            placeholder="Digite seu email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            required
          />

          <PasswordField
            id="password"
            label="Senha"
            placeholder="Digite sua senha"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            required
          />

          <div className="flex justify-end">
            <a href="#" className="text-sm text-primary-light hover:text-cyan-300 transition-colors">
              Esqueci minha senha
            </a>
          </div>

          {error && (
            <p className="text-sm text-red-400 text-center">{error}</p>
          )}

          <Button type="submit" size="lg" disabled={loading} className="w-full justify-center">
            {loading ? 'Entrando...' : 'Entrar'}
          </Button>

          <div className="text-center">
            <Link to="/cadastro" className="text-sm text-gray-400 hover:text-gray-300 transition-colors">
              Criar nova conta
            </Link>
          </div>
        </form>

        <div className="mt-8 pt-6 border-t border-edge">
          <p className="text-xs text-gray-500 text-center">
            GraphTest — Plataforma de análise estrutural de código
          </p>
        </div>
      </AuthCard>
    </div>
  );
}
