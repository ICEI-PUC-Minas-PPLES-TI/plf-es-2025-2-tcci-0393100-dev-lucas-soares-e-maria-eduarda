import { useState } from 'react';
import { Link } from 'react-router-dom';
import { AuthCard } from './AuthCard';
import { FormField } from './FormField';
import { PasswordField } from './PasswordField';

export function LoginCard() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Login attempt:', { email, password });
  };

  return (
    <div className="relative z-10 w-full max-w-105 px-4">
      <AuthCard>
        {/* Header */}
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-semibold text-white mb-2">
            Acessar sua conta
          </h1>
          <p className="text-slate-400 text-sm">
            Entre para utilizar o GraphTest
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <FormField
            id="email"
            label="Email"
            type="email"
            placeholder="Digite seu email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <PasswordField
            id="password"
            label="Senha"
            placeholder="Digite sua senha"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {/* Forgot Password Link */}
          <div className="flex justify-end">
            <a href="#" className="text-sm text-cyan-400 hover:text-cyan-300 transition-colors">
              Esqueci minha senha
            </a>
          </div>

          <button
            type="submit"
            className="w-full py-3 bg-cyan-500 hover:bg-cyan-400 text-white font-medium rounded-lg transition-all hover:shadow-lg hover:shadow-cyan-500/20 active:scale-[0.98]"
          >
            Entrar
          </button>

          <div className="text-center">
            <Link to="/cadastro" className="text-sm text-slate-400 hover:text-slate-300 transition-colors">
              Criar nova conta
            </Link>
          </div>
        </form>

        {/* Footer */}
        <div className="mt-8 pt-6 border-t border-slate-800">
          <p className="text-xs text-slate-500 text-center">
            GraphTest — Plataforma de análise estrutural de código
          </p>
        </div>
      </AuthCard>
    </div>
  );
}