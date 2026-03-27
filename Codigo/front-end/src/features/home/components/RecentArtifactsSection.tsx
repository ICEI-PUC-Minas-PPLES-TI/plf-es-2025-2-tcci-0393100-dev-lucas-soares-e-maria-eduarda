import { FileCode, ExternalLink } from 'lucide-react';
import { motion } from 'motion/react';
import { Button } from '../../../components/Button';

const mockArtifacts = [
  {
    id: 1,
    name: 'calcularMedia()',
    type: 'GFC',
    lastAccess: '2025-11-16',
    typeColor: 'text-blue-400',
    typeBg: 'bg-blue-500/10',
  },
  {
    id: 2,
    name: 'validarEntrada()',
    type: 'GFC',
    lastAccess: '2025-11-15',
    typeColor: 'text-blue-400',
    typeBg: 'bg-blue-500/10',
  },
  {
    id: 3,
    name: 'Sistema de Autenticação',
    type: 'GCE',
    lastAccess: '2025-11-14',
    typeColor: 'text-green-400',
    typeBg: 'bg-green-500/10',
  },
  {
    id: 4,
    name: 'Decisão de Aprovação',
    type: 'Tabela',
    lastAccess: '2025-11-13',
    typeColor: 'text-yellow-400',
    typeBg: 'bg-yellow-500/10',
  },
  {
    id: 5,
    name: 'processarDados()',
    type: 'GFC',
    lastAccess: '2025-11-12',
    typeColor: 'text-blue-400',
    typeBg: 'bg-blue-500/10',
  },
];

export function RecentArtifactsSection() {
  return (
    <section className="container mx-auto px-6 py-16 border-t border-gray-800">
      <h3 className="text-gray-100 text-xl font-semibold mb-8">Últimos Artefatos Acessados</h3>

      <div className="bg-surface-elevated border border-gray-700 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-surface border-b border-gray-700">
              <tr>
                <th className="px-6 py-3 text-left text-xs text-gray-400 uppercase tracking-wider">
                  Nome
                </th>
                <th className="px-6 py-3 text-left text-xs text-gray-400 uppercase tracking-wider">
                  Tipo
                </th>
                <th className="px-6 py-3 text-left text-xs text-gray-400 uppercase tracking-wider">
                  Último Acesso
                </th>
                <th className="px-6 py-3 text-right text-xs text-gray-400 uppercase tracking-wider">
                  Ação
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-700">
              {mockArtifacts.map((artifact, index) => (
                <motion.tr
                  key={artifact.id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.3, delay: index * 0.05 }}
                  className="hover:bg-surface transition-colors"
                >
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center gap-3">
                      <FileCode className="w-4 h-4 text-gray-400" />
                      <span className="text-sm text-gray-100">{artifact.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-3 py-1 rounded-full text-xs font-mono ${artifact.typeBg} ${artifact.typeColor}`}>
                      {artifact.type}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-400">
                    {new Date(artifact.lastAccess).toLocaleDateString('pt-BR')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right">
                    <Button variant="accent" className="inline-flex text-sm">
                      <ExternalLink className="w-4 h-4" />
                      Abrir
                    </Button>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
