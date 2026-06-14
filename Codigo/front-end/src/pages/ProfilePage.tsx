import { useState, useEffect, useMemo, type FormEvent } from 'react';
import { Mail, Shield, Calendar, Save, KeyRound, AlertCircle, CheckCircle2 } from 'lucide-react';
import { Header } from '../components/Header';
import { Button } from '../components/Button';
import { FormField } from '../features/auth/components/FormField';
import { PasswordField } from '../features/auth/components/PasswordField';
import { useAuth } from '../context/AuthContext';
import { decodeToken } from '../utils/jwt';
import UserService from '../services/User/UserService';
import { extractApiErrorMessage } from '../utils/apiError';
import type { UserProfile } from '../services/User/types/user';

const PROFILE_LABELS: Record<number, string> = { 1: 'Administrador', 2: 'Usuário' };
// Mesma regra do backend (UserDTO): minúsculas, dígitos e ponto antes do @.
const EMAIL_REGEX = /^[a-z0-9.]+@[a-z0-9]+\.[a-z]+\.?([a-z]+)?$/;

type Status = 'idle' | 'saving' | 'saved' | 'error';

function initials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

export function ProfilePage() {
  const { token } = useAuth();
  const userId = useMemo(() => decodeToken(token)?.userId ?? null, [token]);

  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [profileStatus, setProfileStatus] = useState<Status>('idle');
  const [profileError, setProfileError] = useState<string | null>(null);

  const [currentPw, setCurrentPw] = useState('');
  const [newPw, setNewPw] = useState('');
  const [confirmPw, setConfirmPw] = useState('');
  const [pwStatus, setPwStatus] = useState<Status>('idle');
  const [pwError, setPwError] = useState<string | null>(null);

  useEffect(() => {
    if (!userId) {
      setLoadError('Não foi possível identificar o usuário logado.');
      setLoading(false);
      return;
    }
    let cancelled = false;
    UserService.buscarPorId(userId)
      .then((u) => {
        if (cancelled) return;
        setUser(u);
        setName(u.name);
        setEmail(u.email);
      })
      .catch(() => { if (!cancelled) setLoadError('Erro ao carregar o perfil.'); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [userId]);

  const profileDirty = !!user && (name.trim() !== user.name || email.trim() !== user.email);

  const handleSaveProfile = async (e: FormEvent) => {
    e.preventDefault();
    if (!userId || !user) return;
    setProfileError(null);

    const trimmedName = name.trim();
    const trimmedEmail = email.trim();
    if (trimmedName.length < 10 || trimmedName.length > 100) {
      setProfileError('O nome deve ter entre 10 e 100 caracteres.');
      return;
    }
    if (!EMAIL_REGEX.test(trimmedEmail)) {
      setProfileError('E-mail inválido. Use apenas minúsculas, no formato nome@dominio.com.');
      return;
    }

    setProfileStatus('saving');
    try {
      await UserService.atualizar(userId, {
        name: trimmedName,
        email: trimmedEmail,
        perfil_usuario: user.perfil_usuario,
      });
      // O PUT devolve só uma mensagem; rebusca para refletir os dados e o updatedAt.
      const fresh = await UserService.buscarPorId(userId);
      setUser(fresh);
      setName(fresh.name);
      setEmail(fresh.email);
      setProfileStatus('saved');
      setTimeout(() => setProfileStatus('idle'), 2000);
    } catch (err) {
      setProfileError(extractApiErrorMessage(err, 'Erro ao salvar as alterações.'));
      setProfileStatus('error');
      setTimeout(() => setProfileStatus('idle'), 3000);
    }
  };

  const handleChangePassword = async (e: FormEvent) => {
    e.preventDefault();
    if (!userId) return;
    setPwError(null);

    if (newPw.length < 8) {
      setPwError('A nova senha deve ter no mínimo 8 caracteres.');
      return;
    }
    if (newPw !== confirmPw) {
      setPwError('A confirmação não corresponde à nova senha.');
      return;
    }
    if (newPw === currentPw) {
      setPwError('A nova senha deve ser diferente da atual.');
      return;
    }

    setPwStatus('saving');
    try {
      await UserService.atualizarSenha(userId, { senha_original: currentPw, senha_atualizada: newPw });
      setCurrentPw('');
      setNewPw('');
      setConfirmPw('');
      setPwStatus('saved');
      setTimeout(() => setPwStatus('idle'), 2000);
    } catch (err) {
      setPwError(extractApiErrorMessage(err, 'Erro ao alterar a senha.'));
      setPwStatus('error');
      setTimeout(() => setPwStatus('idle'), 3000);
    }
  };

  return (
    <div className="min-h-screen bg-surface">
      <Header breadcrumb={[{ label: 'Projetos', href: '/homepage' }, { label: 'Meu Perfil' }]} />

      <div className="container mx-auto px-6 py-10 max-w-3xl">
        <h1 className="text-gray-100 text-2xl font-semibold mb-8">Meu Perfil</h1>

        {loading && (
          <div className="space-y-6">
            <div className="h-40 bg-surface-card border border-edge rounded-lg animate-pulse" />
            <div className="h-64 bg-surface-card border border-edge rounded-lg animate-pulse" />
          </div>
        )}

        {!loading && loadError && (
          <div className="bg-surface-card border border-edge rounded-lg p-8 flex items-center gap-3 justify-center">
            <AlertCircle className="w-5 h-5 text-red-400" />
            <p className="text-red-400">{loadError}</p>
          </div>
        )}

        {!loading && !loadError && user && (
          <div className="space-y-6">
            {/* Cabeçalho com avatar + metadados */}
            <div className="bg-surface-card border border-edge rounded-lg p-6">
              <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-full bg-primary/15 border border-primary/30 flex items-center justify-center text-primary-light text-xl font-semibold shrink-0">
                  {initials(user.name)}
                </div>
                <div className="min-w-0">
                  <h2 className="text-lg font-medium text-gray-100 truncate">{user.name}</h2>
                  <p className="text-sm text-gray-400 flex items-center gap-1.5 truncate">
                    <Mail className="w-3.5 h-3.5 shrink-0" />
                    {user.email}
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-5 pt-5 border-t border-edge text-sm">
                <div className="flex items-center gap-2 text-gray-400">
                  <Shield className="w-4 h-4 text-gray-500" />
                  <span>Perfil:</span>
                  <span className="text-gray-200">
                    {user.perfil_usuario != null ? (PROFILE_LABELS[user.perfil_usuario] ?? 'Usuário') : 'Usuário'}
                  </span>
                </div>
                <div className="flex items-center gap-2 text-gray-400">
                  <Calendar className="w-4 h-4 text-gray-500" />
                  <span>Membro desde:</span>
                  <span className="text-gray-200">
                    {new Date(user.createdAt).toLocaleDateString('pt-BR')}
                  </span>
                </div>
              </div>
            </div>

            {/* Editar dados */}
            <form onSubmit={handleSaveProfile} className="bg-surface-card border border-edge rounded-lg p-6 space-y-4">
              <h3 className="text-base font-semibold text-gray-200">Dados da conta</h3>

              <FormField
                id="name"
                label="Nome"
                placeholder="Seu nome completo"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
              <FormField
                id="email"
                label="E-mail"
                type="email"
                placeholder="nome@dominio.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />

              {profileError && (
                <p className="text-sm text-red-400 flex items-center gap-1.5">
                  <AlertCircle className="w-4 h-4 shrink-0" />
                  {profileError}
                </p>
              )}

              <div className="flex items-center gap-3">
                <Button type="submit" disabled={!profileDirty || profileStatus === 'saving'}>
                  <Save className="w-4 h-4" />
                  {profileStatus === 'saving' ? 'Salvando...' : 'Salvar alterações'}
                </Button>
                {profileStatus === 'saved' && (
                  <span className="text-sm text-green-400 flex items-center gap-1.5">
                    <CheckCircle2 className="w-4 h-4" />
                    Salvo!
                  </span>
                )}
              </div>
            </form>

            {/* Alterar senha */}
            <form onSubmit={handleChangePassword} className="bg-surface-card border border-edge rounded-lg p-6 space-y-4">
              <h3 className="text-base font-semibold text-gray-200">Alterar senha</h3>

              <PasswordField
                id="current-password"
                label="Senha atual"
                placeholder="Sua senha atual"
                value={currentPw}
                onChange={(e) => setCurrentPw(e.target.value)}
              />
              <PasswordField
                id="new-password"
                label="Nova senha"
                placeholder="Mínimo 8 caracteres"
                value={newPw}
                onChange={(e) => setNewPw(e.target.value)}
              />
              <PasswordField
                id="confirm-password"
                label="Confirmar nova senha"
                placeholder="Repita a nova senha"
                value={confirmPw}
                onChange={(e) => setConfirmPw(e.target.value)}
              />

              {pwError && (
                <p className="text-sm text-red-400 flex items-center gap-1.5">
                  <AlertCircle className="w-4 h-4 shrink-0" />
                  {pwError}
                </p>
              )}

              <div className="flex items-center gap-3">
                <Button
                  type="submit"
                  disabled={pwStatus === 'saving' || !currentPw || !newPw || !confirmPw}
                >
                  <KeyRound className="w-4 h-4" />
                  {pwStatus === 'saving' ? 'Alterando...' : 'Alterar senha'}
                </Button>
                {pwStatus === 'saved' && (
                  <span className="text-sm text-green-400 flex items-center gap-1.5">
                    <CheckCircle2 className="w-4 h-4" />
                    Senha alterada!
                  </span>
                )}
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
