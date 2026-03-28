import { AlertTriangle, XCircle, AlertCircle, ChevronRight } from 'lucide-react';
import { SectionHeader } from '../../../components/SectionHeader';

type Severity = 'high' | 'medium' | 'low';

interface Warning {
  id: string;
  title: string;
  description: string;
  artifact: string;
  severity: Severity;
}

const warnings: Warning[] = [
  {
    id: '1',
    title: 'Erro de parsing no GFC',
    description: 'Método processarDados() contém estrutura não reconhecida na linha 47',
    artifact: 'Processador.java',
    severity: 'high',
  },
  {
    id: '2',
    title: 'Inconsistência detectada no GCE',
    description: 'Nó de efeito "E3" está desconectado do grafo principal',
    artifact: 'Validação de Login',
    severity: 'medium',
  },
  {
    id: '3',
    title: 'Tabela de decisão incompleta',
    description: '3 regras não possuem ações definidas',
    artifact: 'Tabela_Cadastro_v1',
    severity: 'medium',
  },
  {
    id: '4',
    title: 'GFC sem tabela associada',
    description: 'O grafo validarCampos() ainda não gerou tabela de decisão',
    artifact: 'validarCampos()',
    severity: 'low',
  },
];

const severityConfig = {
  high: {
    icon: XCircle,
    iconColor: 'text-red-400',
    borderColor: 'border-red-900/50 hover:border-red-800',
    bgHover: 'hover:bg-red-950/20',
  },
  medium: {
    icon: AlertTriangle,
    iconColor: 'text-yellow-400',
    borderColor: 'border-yellow-900/50 hover:border-yellow-800',
    bgHover: 'hover:bg-yellow-950/20',
  },
  low: {
    icon: AlertCircle,
    iconColor: 'text-blue-400',
    borderColor: 'border-blue-900/50 hover:border-blue-800',
    bgHover: 'hover:bg-blue-950/20',
  },
};

export function ValidationWarnings() {
  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader
        title="Avisos e Validações"
        action={
          <span className="text-xs text-gray-500">
            {warnings.length} {warnings.length === 1 ? 'item' : 'itens'}
          </span>
        }
      />

      <div className="space-y-2">
        {warnings.map((warning) => {
          const config = severityConfig[warning.severity];
          const Icon = config.icon;

          return (
            <button
              key={warning.id}
              className={`
                w-full text-left bg-surface border rounded-lg p-4
                ${config.borderColor} ${config.bgHover}
                transition-all group cursor-pointer
                flex items-start gap-3
              `}
            >
              <div className={`mt-0.5 ${config.iconColor}`}>
                <Icon className="w-5 h-5" />
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2 mb-1">
                  <h3 className="text-sm">{warning.title}</h3>
                  <ChevronRight className="w-4 h-4 text-gray-600 group-hover:text-primary-light transition-colors shrink-0" />
                </div>
                <p className="text-xs text-gray-400 mb-2">{warning.description}</p>
                <div className="flex items-center gap-2">
                  <span className="text-xs text-gray-600">Artefato:</span>
                  <span className="text-xs text-primary-light">{warning.artifact}</span>
                </div>
              </div>
            </button>
          );
        })}
      </div>

      {warnings.length === 0 && (
        <div className="text-center py-8 text-gray-500">
          <AlertCircle className="w-8 h-8 mx-auto mb-2 opacity-50" />
          <p className="text-sm">Nenhum aviso ou erro detectado</p>
        </div>
      )}
    </div>
  );
}
