import { useState, type SyntheticEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthCard } from './AuthCard';
import { FormField } from './FormField';
import { PasswordField } from './PasswordField';
import { Button } from '../../../components/Button';
import UserService from '../../../services/User/UserService';

export function SignUpCard() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await UserService.criar(formData);
      navigate('/login');
    } catch {
      setError('Erro ao criar conta. Verifique os dados e tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-105 mx-auto px-4">
      <AuthCard>
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-semibold text-white mb-2">
            Criar nova conta
          </h1>
          <p className="text-gray-400">
            Cadastre-se para começar a usar o GraphTest
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <FormField
            id="name"
            label="Nome"
            placeholder="Digite seu nome completo"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
          />

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
            placeholder="Crie uma senha"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            required
          />

          {error && (
            <p className="text-sm text-red-400 text-center">{error}</p>
          )}

          <Button type="submit" size="lg" disabled={loading} className="w-full justify-center mt-6">
            {loading ? 'Criando conta...' : 'Criar conta'}
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-gray-400">
            Já possui uma conta?{' '}
            <Link to="/login" className="text-primary-light hover:text-cyan-300 font-medium transition-colors">
              Entrar
            </Link>
          </p>
        </div>
      </AuthCard>

      <div className="mt-8 text-center">
        <p className="text-sm text-gray-500">
          GraphTest — Plataforma de análise estrutural de código Java
        </p>
      </div>
    </div>
  );
}
