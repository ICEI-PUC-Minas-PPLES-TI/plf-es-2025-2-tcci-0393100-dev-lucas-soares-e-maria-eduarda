import { useState, type SyntheticEvent } from 'react';
import { X } from 'lucide-react';
import { Button } from '../../../components/Button';
import ProjectService from '../../../services/Project/ProjectService';

interface CreateProjectModalProps {
  onClose: () => void;
  onCreated: (id: string) => void;
}

export function CreateProjectModal({ onClose, onCreated }: CreateProjectModalProps) {
  const [formData, setFormData] = useState({ name: '', description: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await ProjectService.criar(formData);
      onCreated(response.id_projeto);
    } catch {
      setError('Erro ao criar projeto. Verifique os dados e tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/60" onClick={onClose} />

      <div className="relative bg-surface-elevated border border-edge rounded-lg w-full max-w-md mx-4 p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-100">Criar novo projeto</h2>
          <button
            onClick={onClose}
            className="p-1 hover:bg-surface-hover rounded transition-colors"
          >
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>

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
              {loading ? 'Criando...' : 'Criar projeto'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
