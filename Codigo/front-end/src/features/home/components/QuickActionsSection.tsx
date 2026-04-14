import { GitBranch, Network, Table2 } from 'lucide-react';
import { motion } from 'motion/react';

const quickActions = [
  {
    id: 1,
    title: 'Gerar GFC',
    description: 'Grafo de Fluxo de Controle a partir de código importado',
    icon: GitBranch,
    bgColor: 'bg-blue-500/10',
    hoverColor: 'hover:bg-blue-500/20',
    iconColor: 'text-blue-400',
    borderHover: 'hover:border-blue-500',
  },
  {
    id: 2,
    title: 'Modelar GCE',
    description: 'Grafo de Causa e Efeito para análise funcional',
    icon: Network,
    bgColor: 'bg-green-500/10',
    hoverColor: 'hover:bg-green-500/20',
    iconColor: 'text-green-400',
    borderHover: 'hover:border-green-500',
  },
  {
    id: 3,
    title: 'Gerar Tabela de Decisão',
    description: 'Derive tabelas a partir de condições e ações',
    icon: Table2,
    bgColor: 'bg-yellow-500/10',
    hoverColor: 'hover:bg-yellow-500/20',
    iconColor: 'text-yellow-400',
    borderHover: 'hover:border-yellow-500',
  },
];

interface QuickActionsSectionProps {
  onCreateGCE?: () => void;
  onGenerateTable?: () => void;
}

export function QuickActionsSection({ onCreateGCE, onGenerateTable }: QuickActionsSectionProps) {
  const handleClick = (id: number) => {
    if (id === 2) onCreateGCE?.();
    if (id === 3) onGenerateTable?.();
  };

  return (
    <section className="container mx-auto px-6 py-16 border-t border-edge">
      <h3 className="text-gray-100 text-xl font-semibold mb-8">Ações Rápidas</h3>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {quickActions.map((action, index) => (
          <motion.button
            key={action.id}
            onClick={() => handleClick(action.id)}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: index * 0.1 }}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className={`${action.bgColor} ${action.hoverColor} ${action.borderHover} border border-edge rounded-lg p-8 text-left transition-all group`}
          >
            <div className={`inline-flex p-4 ${action.bgColor} rounded-lg mb-4`}>
              <action.icon className={`w-8 h-8 ${action.iconColor}`} />
            </div>

            <h4 className="text-gray-100 font-medium mb-2 group-hover:text-white transition-colors">
              {action.title}
            </h4>
            <p className="text-sm text-gray-400">{action.description}</p>
          </motion.button>
        ))}
      </div>
    </section>
  );
}
