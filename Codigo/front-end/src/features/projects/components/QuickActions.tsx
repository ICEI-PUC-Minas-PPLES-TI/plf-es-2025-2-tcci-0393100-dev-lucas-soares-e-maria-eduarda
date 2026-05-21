import { GitBranch, Upload, Network, Table } from 'lucide-react';
import { SectionHeader } from '../../../components/SectionHeader';
import { useTheme } from '../../../context/ThemeContext';

interface QuickActionsProps {
  onCreateGCE?: () => void;
  onGenerateTable?: () => void;
  onCreateGFC?: () => void;
}

const actions = [
  {
    id: 'gfc',
    title: 'Gerar GFC',
    description: 'Criar Grafo de Fluxo de Controle a partir de código',
    icon: GitBranch,
    darkClass: 'bg-linear-to-br from-cyan-600 to-blue-600 hover:from-cyan-500 hover:to-blue-500',
    lightBg: 'bg-blue-500/10 hover:bg-blue-500/20 hover:border-blue-400',
    lightIconColor: 'text-blue-600',
  },
  {
    id: 'import',
    title: 'Importar Arquivo',
    description: 'Adicionar arquivo Java ou código-fonte',
    icon: Upload,
    darkClass: 'bg-linear-to-br from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500',
    lightBg: 'bg-indigo-500/10 hover:bg-indigo-500/20 hover:border-indigo-400',
    lightIconColor: 'text-indigo-600',
  },
  {
    id: 'gce',
    title: 'Modelar GCE',
    description: 'Criar Grafo de Causa e Efeito manualmente',
    icon: Network,
    darkClass: 'bg-linear-to-br from-green-600 to-emerald-600 hover:from-green-500 hover:to-emerald-500',
    lightBg: 'bg-green-500/10 hover:bg-green-500/20 hover:border-green-400',
    lightIconColor: 'text-green-600',
  },
  {
    id: 'table',
    title: 'Gerar Tabela',
    description: 'Criar Tabela de Decisão a partir de grafo',
    icon: Table,
    darkClass: 'bg-linear-to-br from-yellow-600 to-orange-600 hover:from-yellow-500 hover:to-orange-500',
    lightBg: 'bg-yellow-500/10 hover:bg-yellow-500/20 hover:border-yellow-400',
    lightIconColor: 'text-yellow-600',
  },
];

export function QuickActions({ onCreateGCE, onGenerateTable, onCreateGFC }: QuickActionsProps) {
  const { theme } = useTheme();
  const isLight = theme === 'light';

  const handleClick = (id: string) => {
    if (id === 'gce') onCreateGCE?.();
    if (id === 'table') onGenerateTable?.();
    if (id === 'gfc') onCreateGFC?.();
  };

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader title="Ações Rápidas" />

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {actions.map((action) => {
          const Icon = action.icon;
          return (
            <button
              key={action.id}
              onClick={() => handleClick(action.id)}
              className={`
                relative overflow-hidden rounded-lg p-4 text-left
                transition-all hover:scale-105 hover:shadow-lg group border
                ${isLight
                  ? `${action.lightBg} border-edge`
                  : `bg-linear-to-br ${action.darkClass} border-transparent`
                }
              `}
            >
              <div className="relative z-10">
                <div className="mb-3">
                  <Icon className={`w-6 h-6 ${isLight ? action.lightIconColor : 'text-white'}`} />
                </div>
                <h3 className={`mb-1 font-semibold ${isLight ? 'text-gray-200' : 'text-white'}`}>
                  {action.title}
                </h3>
                <p className={`text-xs ${isLight ? 'text-gray-400' : 'text-white/80'}`}>
                  {action.description}
                </p>
              </div>

              {!isLight && (
                <div className="absolute inset-0 bg-linear-to-br from-white/0 to-white/0 group-hover:from-white/10 group-hover:to-transparent transition-all" />
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}
