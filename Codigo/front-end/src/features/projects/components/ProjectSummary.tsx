import { TrendingUp } from 'lucide-react';
import { SectionHeader } from '../../../components/SectionHeader';
import { ARTIFACT_TYPES } from '../../../shared/artifactTypes';

const stats = [
  { label: 'Artefatos', value: 12, icon: ARTIFACT_TYPES.CODE.icon, color: ARTIFACT_TYPES.CODE.color },
  { label: 'GFCs', value: 8, icon: ARTIFACT_TYPES.GFC.icon, color: ARTIFACT_TYPES.GFC.color },
  { label: 'GCEs', value: 5, icon: ARTIFACT_TYPES.GCE.icon, color: ARTIFACT_TYPES.GCE.color },
  { label: 'Tabelas de Decisão', value: 3, icon: ARTIFACT_TYPES.TABLE.icon, color: ARTIFACT_TYPES.TABLE.color },
];

export function ProjectSummary() {
  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader title="Resumo do Projeto" icon={TrendingUp} />

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
            className="bg-linear-to-r from-cyan-500 to-blue-500 h-full rounded-full"
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
