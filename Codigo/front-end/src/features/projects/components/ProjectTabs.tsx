import { LayoutDashboard, FileCode, GitBranch, Network, Table } from 'lucide-react';

interface ProjectTabsProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
}

const tabs = [
  { id: 'overview', label: 'Visão Geral', icon: LayoutDashboard },
  { id: 'artifacts', label: 'Artefatos de Código', icon: FileCode },
  { id: 'gfc', label: 'GFC — Fluxo de Controle', icon: GitBranch },
  { id: 'gce', label: 'GCE — Causa e Efeito', icon: Network },
  { id: 'tables', label: 'Tabelas de Decisão', icon: Table },
];

export function ProjectTabs({ activeTab, onTabChange }: ProjectTabsProps) {
  return (
    <div className="container mx-auto px-6 flex gap-1 border-b border-edge">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;

          return (
            <button
              key={tab.id}
              onClick={() => onTabChange(tab.id)}
              className={`
                px-4 py-3 flex items-center gap-2 text-sm relative
                transition-colors border-b-2
                ${isActive
                  ? 'text-primary-light border-primary'
                  : 'text-gray-400 border-transparent hover:text-gray-300 hover:bg-surface-card'
                }
              `}
            >
              <Icon className="w-4 h-4" />
              {tab.label}
            </button>
          );
        })}
    </div>
  );
}
