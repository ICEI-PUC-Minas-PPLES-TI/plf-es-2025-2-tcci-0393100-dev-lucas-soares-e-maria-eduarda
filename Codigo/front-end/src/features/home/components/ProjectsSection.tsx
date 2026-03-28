import { useState } from 'react';
import { Calendar, Folder, MoreVertical, ExternalLink, Edit2, Trash2, Download } from 'lucide-react';
import { motion } from 'motion/react';
import { Button } from '../../../components/Button';
import { SearchBar } from '../../../components/SearchBar';

const mockProjects = [
  {
    id: 1,
    name: 'Sistema de Login',
    createdAt: '2025-11-10',
    artifacts: 8,
  },
  {
    id: 2,
    name: 'Calculadora Científica',
    createdAt: '2025-11-08',
    artifacts: 12,
  },
  {
    id: 3,
    name: 'Ordenação de Arrays',
    createdAt: '2025-11-05',
    artifacts: 5,
  },
  {
    id: 4,
    name: 'Busca em Grafos',
    createdAt: '2025-11-01',
    artifacts: 15,
  },
];

export function ProjectsSection() {
  const [searchTerm, setSearchTerm] = useState('');
  const [openMenu, setOpenMenu] = useState<number | null>(null);

  return (
    <section className="container mx-auto px-6 py-16" id="projetos">
      <div className="mb-8">
        <h3 className="text-gray-100 text-xl font-semibold mb-6">Seus Projetos</h3>

        <div className="flex flex-col sm:flex-row gap-4 mb-6">
          <SearchBar
            value={searchTerm}
            onValueChange={setSearchTerm}
            placeholder="Buscar projeto..."
          />
          <Button variant="outline">
            <Calendar className="w-4 h-4" />
            Filtrar por data
          </Button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {mockProjects.map((project, index) => (
            <motion.div
              key={project.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: index * 0.1 }}
              className="bg-surface-elevated border border-edge rounded-lg p-5 hover:border-primary transition-colors group"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="p-2 bg-primary/10 rounded-lg">
                  <Folder className="w-5 h-5 text-primary-light" />
                </div>
                <div className="relative">
                  <button
                    onClick={() => setOpenMenu(openMenu === project.id ? null : project.id)}
                    className="p-1 hover:bg-surface-hover rounded transition-colors"
                  >
                    <MoreVertical className="w-4 h-4 text-gray-400" />
                  </button>

                  {openMenu === project.id && (
                    <div className="absolute right-0 mt-2 w-40 bg-surface border border-edge rounded-lg shadow-xl z-10">
                      <button className="w-full px-4 py-2 text-left text-sm text-gray-300 hover:bg-surface-hover flex items-center gap-2">
                        <Edit2 className="w-3 h-3" />
                        Renomear
                      </button>
                      <button className="w-full px-4 py-2 text-left text-sm text-gray-300 hover:bg-surface-hover flex items-center gap-2">
                        <Download className="w-3 h-3" />
                        Exportar
                      </button>
                      <button className="w-full px-4 py-2 text-left text-sm text-red-400 hover:bg-surface-hover flex items-center gap-2">
                        <Trash2 className="w-3 h-3" />
                        Excluir
                      </button>
                    </div>
                  )}
                </div>
              </div>

              <h4 className="text-gray-100 font-medium mb-2">{project.name}</h4>

              <div className="space-y-2 mb-4">
                <p className="text-sm text-gray-400">
                  Criado em {new Date(project.createdAt).toLocaleDateString('pt-BR')}
                </p>
                <p className="text-sm text-gray-400">
                  {project.artifacts} artefatos
                </p>
              </div>

              <Button variant="accent" className="w-full justify-center group-hover:bg-primary group-hover:text-white">
                <ExternalLink className="w-4 h-4" />
                Abrir
              </Button>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
