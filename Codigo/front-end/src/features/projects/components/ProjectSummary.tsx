import { FileCode, GitBranch, Network, Table, TrendingUp } from 'lucide-react';

const stats = [
  { label: 'Artefatos', value: 12, icon: FileCode, color: 'text-blue-400' },
  { label: 'GFCs', value: 8, icon: GitBranch, color: 'text-cyan-400' },
  { label: 'GCEs', value: 5, icon: Network, color: 'text-green-400' },
  { label: 'Tabelas de Decisão', value: 3, icon: Table, color: 'text-yellow-400' },
];

export function ProjectSummary() {
  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <div className="flex items-center gap-2 mb-4">
        <TrendingUp className="w-5 h-5 text-primary-light" />
        <h2 className="text-lg">Resumo do Projeto</h2>
      </div>

      <div className="space-y-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 rounded bg-surface border border-edge flex items-center justify-center ${stat.color}`}>
                  <Icon className="w-5 h-5" />
                </div>
                <span className="text-sm text-gray-300">{stat.label}</span>
              </div>
              <span className="text-2xl">{stat.value}</span>
            </div>
          );
        })}
      </div>

      <div className="mt-6 pt-4 border-t border-edge">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm text-gray-400">Progresso Geral</span>
          <span className="text-sm text-primary-light">68%</span>
        </div>
        <div className="w-full bg-surface rounded-full h-2 overflow-hidden">
          <div
            className="bg-gradient-to-r from-cyan-500 to-blue-500 h-full rounded-full"
            style={{ width: '68%' }}
          />
        </div>
      </div>

      <div className="mt-4 grid grid-cols-2 gap-3">
        <div className="bg-surface border border-edge rounded p-3">
          <p className="text-xs text-gray-500 mb-1">Última atualização</p>
          <p className="text-sm">2 horas atrás</p>
        </div>
        <div className="bg-surface border border-edge rounded p-3">
          <p className="text-xs text-gray-500 mb-1">Colaboradores</p>
          <p className="text-sm">4 membros</p>
        </div>
      </div>
    </div>
  );
}
