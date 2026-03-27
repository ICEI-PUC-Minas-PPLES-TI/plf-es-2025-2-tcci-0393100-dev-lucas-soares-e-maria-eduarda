import { GitBranch, Upload, Network, Table } from 'lucide-react';

const actions = [
  {
    id: 'gfc',
    title: 'Gerar GFC',
    description: 'Criar Grafo de Fluxo de Controle a partir de código',
    icon: GitBranch,
    color: 'from-cyan-600 to-blue-600',
    hoverColor: 'hover:from-cyan-500 hover:to-blue-500',
  },
  {
    id: 'import',
    title: 'Importar Arquivo',
    description: 'Adicionar arquivo Java ou código-fonte',
    icon: Upload,
    color: 'from-blue-600 to-indigo-600',
    hoverColor: 'hover:from-blue-500 hover:to-indigo-500',
  },
  {
    id: 'gce',
    title: 'Modelar GCE',
    description: 'Criar Grafo de Causa e Efeito manualmente',
    icon: Network,
    color: 'from-green-600 to-emerald-600',
    hoverColor: 'hover:from-green-500 hover:to-emerald-500',
  },
  {
    id: 'table',
    title: 'Gerar Tabela',
    description: 'Criar Tabela de Decisão a partir de grafo',
    icon: Table,
    color: 'from-yellow-600 to-orange-600',
    hoverColor: 'hover:from-yellow-500 hover:to-orange-500',
  },
];

export function QuickActions() {
  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <h2 className="text-lg mb-4">Ações Rápidas</h2>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {actions.map((action) => {
          const Icon = action.icon;
          return (
            <button
              key={action.id}
              className={`
                relative overflow-hidden rounded-lg p-4 text-left
                bg-gradient-to-br ${action.color} ${action.hoverColor}
                transition-all hover:scale-105 hover:shadow-lg
                group
              `}
            >
              <div className="relative z-10">
                <div className="mb-3">
                  <Icon className="w-6 h-6" />
                </div>
                <h3 className="mb-1">{action.title}</h3>
                <p className="text-xs text-white/80">{action.description}</p>
              </div>

              <div className="absolute inset-0 bg-gradient-to-br from-white/0 to-white/0 group-hover:from-white/10 group-hover:to-transparent transition-all" />
            </button>
          );
        })}
      </div>
    </div>
  );
}
