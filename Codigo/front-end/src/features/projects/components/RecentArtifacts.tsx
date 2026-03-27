import { FileCode, GitBranch, Network, Table, ExternalLink, Clock } from 'lucide-react';
import { Button } from '../../../components/Button';

const artifacts = [
  {
    id: '1',
    name: 'calcularMedia()',
    typeLabel: 'Grafo de Fluxo',
    icon: GitBranch,
    lastAccess: '2 horas atrás',
    file: 'Calculadora.java',
    color: 'text-cyan-400',
    bgColor: 'bg-cyan-500/10',
  },
  {
    id: '2',
    name: 'Validação de Login',
    typeLabel: 'Grafo Causa-Efeito',
    icon: Network,
    lastAccess: '5 horas atrás',
    file: 'Modelagem manual',
    color: 'text-green-400',
    bgColor: 'bg-green-500/10',
  },
  {
    id: '3',
    name: 'validarEntrada()',
    typeLabel: 'Grafo de Fluxo',
    icon: GitBranch,
    lastAccess: '1 dia atrás',
    file: 'Validador.java',
    color: 'text-cyan-400',
    bgColor: 'bg-cyan-500/10',
  },
  {
    id: '4',
    name: 'Tabela_Cadastro_v1',
    typeLabel: 'Tabela de Decisão',
    icon: Table,
    lastAccess: '1 dia atrás',
    file: 'Derivado de GCE',
    color: 'text-yellow-400',
    bgColor: 'bg-yellow-500/10',
  },
  {
    id: '5',
    name: 'Calculadora.java',
    typeLabel: 'Artefato de Código',
    icon: FileCode,
    lastAccess: '2 dias atrás',
    file: '8 métodos • 245 linhas',
    color: 'text-blue-400',
    bgColor: 'bg-blue-500/10',
  },
];

export function RecentArtifacts() {
  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg">Artefatos Recentes</h2>
        <Button variant="ghost" size="sm" className="text-primary-light hover:bg-primary/10">
          Ver todos
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-3">
        {artifacts.map((artifact) => {
          const Icon = artifact.icon;
          return (
            <div
              key={artifact.id}
              className="bg-surface border border-edge rounded-lg p-4 hover:border-edge-hover transition-colors cursor-pointer group"
            >
              <div className="flex items-start justify-between mb-3">
                <div className={`w-10 h-10 rounded ${artifact.bgColor} border border-edge flex items-center justify-center ${artifact.color}`}>
                  <Icon className="w-5 h-5" />
                </div>
                <button className="opacity-0 group-hover:opacity-100 transition-opacity text-gray-400 hover:text-primary-light">
                  <ExternalLink className="w-4 h-4" />
                </button>
              </div>

              <h3 className="text-sm mb-1 truncate" title={artifact.name}>
                {artifact.name}
              </h3>
              <p className="text-xs text-gray-500 mb-2">{artifact.typeLabel}</p>
              <p className="text-xs text-gray-600 mb-3 truncate" title={artifact.file}>
                {artifact.file}
              </p>

              <div className="flex items-center gap-1 text-xs text-gray-500">
                <Clock className="w-3 h-3" />
                {artifact.lastAccess}
              </div>

              <Button size="sm" className="w-full mt-3 justify-center">
                Abrir
              </Button>
            </div>
          );
        })}
      </div>
    </div>
  );
}
