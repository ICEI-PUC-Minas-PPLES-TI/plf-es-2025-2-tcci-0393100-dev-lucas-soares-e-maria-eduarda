import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Folder, MoreVertical, ExternalLink, Edit2, Trash2, Download } from 'lucide-react';
import { motion } from 'motion/react';
import { Button } from '../../../components/Button';
import { SearchBar } from '../../../components/SearchBar';
import ProjectService from '../../../services/Project/ProjectService';
import type { ProjectDTO } from '../../../services/Project/types/project';

export function ProjectsSection() {
  const [searchTerm, setSearchTerm] = useState('');
  const [openMenu, setOpenMenu] = useState<string | null>(null);
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    ProjectService.listarMeus()
      .then((data) => {
        if (!cancelled) setProjects(data);
      })
      .catch(() => {
        if (!cancelled) setError('Erro ao carregar projetos.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => { cancelled = true; };
  }, []);

  const handleDelete = async (id: string) => {
    try {
      await ProjectService.excluir(id);
      setProjects((prev) => prev.filter((p) => p.id !== id));
      setOpenMenu(null);
    } catch {
      setError('Erro ao excluir projeto.');
    }
  };

  const filteredProjects = projects.filter((p) =>
    p.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

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

        {loading && (
          <p className="text-gray-400 text-center py-8">Carregando projetos...</p>
        )}

        {error && (
          <p className="text-red-400 text-center py-8">{error}</p>
        )}

        {!loading && !error && filteredProjects.length === 0 && (
          <p className="text-gray-400 text-center py-8">Nenhum projeto encontrado.</p>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {filteredProjects.map((project, index) => (
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
                      <button
                        onClick={() => handleDelete(project.id)}
                        className="w-full px-4 py-2 text-left text-sm text-red-400 hover:bg-surface-hover flex items-center gap-2"
                      >
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

              <Link to={`/projeto/${project.id}`}>
                <Button variant="accent" className="w-full justify-center group-hover:bg-primary group-hover:text-white">
                  <ExternalLink className="w-4 h-4" />
                  Abrir
                </Button>
              </Link>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
