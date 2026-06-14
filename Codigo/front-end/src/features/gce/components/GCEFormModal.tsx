import { useState, type SyntheticEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { Modal } from '../../../components/Modal';
import { Button } from '../../../components/Button';
import ProjectService from '../../../services/Project/ProjectService';
import type { ProjectDTO } from '../../../services/Project/types/project';

interface GCEFormModalProps {
  projectId?: string;
  onClose: () => void;
}

export function GCEFormModal({ projectId: initialProjectId, onClose }: GCEFormModalProps) {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedProjectId, setSelectedProjectId] = useState(initialProjectId ?? '');
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [loadingProjects, setLoadingProjects] = useState(!initialProjectId);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (initialProjectId) return;
    ProjectService.listarMeus()
      .then(setProjects)
      .catch(() => setError('Erro ao carregar projetos.'))
      .finally(() => setLoadingProjects(false));
  }, [initialProjectId]);

  const handleSubmit = (e: SyntheticEvent) => {
    e.preventDefault();
    if (!selectedProjectId) {
      setError('Selecione um projeto.');
      return;
    }
    onClose();
    navigate(`/projeto/${selectedProjectId}/gce/new`, {
      state: { name: name.trim(), description: description.trim() },
    });
  };

  return (
    <Modal title="Criar novo GCE" onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {!initialProjectId && (
          <div>
            <label htmlFor="gce-project" className="block text-sm text-gray-300 mb-1.5">
              Projeto
            </label>
            {loadingProjects ? (
              <p className="text-sm text-gray-500">Carregando projetos...</p>
            ) : (
              <select
                id="gce-project"
                value={selectedProjectId}
                onChange={(e) => setSelectedProjectId(e.target.value)}
                required
                className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 focus:border-primary focus:outline-none transition-colors"
              >
                <option value="">Selecione um projeto...</option>
                {projects.map((p) => (
                  <option key={p.id} value={p.id}>{p.name}</option>
                ))}
              </select>
            )}
          </div>
        )}

        <div>
          <label htmlFor="gce-name" className="block text-sm text-gray-300 mb-1.5">
            Nome
          </label>
          <input
            id="gce-name"
            type="text"
            placeholder="Ex: GCE de login"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            minLength={3}
            maxLength={80}
            className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 placeholder-gray-500 focus:border-primary focus:outline-none transition-colors"
          />
        </div>

        <div>
          <label htmlFor="gce-description" className="block text-sm text-gray-300 mb-1.5">
            Descrição <span className="text-gray-500">(opcional)</span>
          </label>
          <textarea
            id="gce-description"
            placeholder="Descreva brevemente o GCE"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            maxLength={200}
            rows={3}
            className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 placeholder-gray-500 focus:border-primary focus:outline-none transition-colors resize-none"
          />
        </div>

        {error && <p className="text-sm text-red-400 text-center">{error}</p>}

        <div className="flex gap-3 pt-2">
          <Button type="button" variant="ghost" className="flex-1 justify-center" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" className="flex-1 justify-center">
            Criar GCE
          </Button>
        </div>
      </form>
    </Modal>
  );
}
