import { useState, type SyntheticEvent } from 'react';
import { Modal } from '../../../components/Modal';
import { Button } from '../../../components/Button';
import ProjectService from '../../../services/Project/ProjectService';
import type { ProjectDTO } from '../../../services/Project/types/project';

interface ProjectFormModalProps {
  project?: ProjectDTO;
  onClose: () => void;
  onSuccess: (project: ProjectDTO) => void;
}

export function ProjectFormModal({ project, onClose, onSuccess }: ProjectFormModalProps) {
  const isEditing = !!project;

  const [formData, setFormData] = useState({
    name: project?.name ?? '',
    description: project?.description ?? '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (isEditing) {
        await ProjectService.atualizar(project.id, formData);
        onSuccess({ ...project, ...formData });
      } else {
        const response = await ProjectService.criar(formData);
        onSuccess({ id: response.id_projeto, ...formData });
      }
    } catch {
      setError(
        isEditing
          ? 'Erro ao atualizar projeto. Verifique os dados e tente novamente.'
          : 'Erro ao criar projeto. Verifique os dados e tente novamente.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={isEditing ? 'Editar projeto' : 'Criar novo projeto'} onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="project-name" className="block text-sm text-gray-300 mb-1.5">
            Nome
          </label>
          <input
            id="project-name"
            type="text"
            placeholder="Ex: Sistema de Login"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
            minLength={3}
            maxLength={50}
            className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 placeholder-gray-500 focus:border-primary focus:outline-none transition-colors"
          />
        </div>

        <div>
          <label htmlFor="project-description" className="block text-sm text-gray-300 mb-1.5">
            Descrição
          </label>
          <textarea
            id="project-description"
            placeholder="Descreva brevemente o projeto"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            required
            maxLength={200}
            rows={3}
            className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 placeholder-gray-500 focus:border-primary focus:outline-none transition-colors resize-none"
          />
        </div>

        {error && (
          <p className="text-sm text-red-400 text-center">{error}</p>
        )}

        <div className="flex gap-3 pt-2">
          <Button
            type="button"
            variant="ghost"
            className="flex-1 justify-center"
            onClick={onClose}
          >
            Cancelar
          </Button>
          <Button
            type="submit"
            disabled={loading}
            className="flex-1 justify-center"
          >
            {loading
              ? (isEditing ? 'Salvando...' : 'Criando...')
              : (isEditing ? 'Salvar alterações' : 'Criar projeto')
            }
          </Button>
        </div>
      </form>
    </Modal>
  );
}
