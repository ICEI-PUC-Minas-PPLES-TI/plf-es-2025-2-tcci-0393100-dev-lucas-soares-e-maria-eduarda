import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Modal } from '../../../components/Modal';
import { Button } from '../../../components/Button';
import GCEService from '../../../services/GCE/GCEService';
import ProjectService from '../../../services/Project/ProjectService';
import type { GCEDTO } from '../../gce/types/gce';
import type { ProjectDTO } from '../../../services/Project/types/project';

interface SelectGCEForTableModalProps {
  /** When provided, skips the project selector and only lists GCEs from this project. */
  projectId?: string;
  onClose: () => void;
}

export function SelectGCEForTableModal({ projectId: initialProjectId, onClose }: SelectGCEForTableModalProps) {
  const navigate = useNavigate();

  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState(initialProjectId ?? '');
  const [gces, setGces] = useState<GCEDTO[]>([]);
  const [selectedGceId, setSelectedGceId] = useState('');
  const [loadingProjects, setLoadingProjects] = useState(!initialProjectId);
  const [loadingGces, setLoadingGces] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load projects when no projectId given (homepage context)
  useEffect(() => {
    if (initialProjectId) return;
    ProjectService.listarMeus()
      .then(setProjects)
      .catch(() => setError('Erro ao carregar projetos.'))
      .finally(() => setLoadingProjects(false));
  }, [initialProjectId]);

  // Load GCEs when project is selected
  useEffect(() => {
    if (!selectedProjectId) {
      setGces([]);
      setSelectedGceId('');
      return;
    }
    setLoadingGces(true);
    setSelectedGceId('');
    GCEService.listarPorProjeto(selectedProjectId)
      .then(setGces)
      .catch(() => setError('Erro ao carregar GCEs.'))
      .finally(() => setLoadingGces(false));
  }, [selectedProjectId]);

  const handleOpen = () => {
    if (!selectedProjectId || !selectedGceId) {
      setError('Selecione um projeto e um GCE.');
      return;
    }
    onClose();
    navigate(`/projeto/${selectedProjectId}/gce/${selectedGceId}/tabela-decisao`);
  };

  return (
    <Modal title="Gerar Tabela de Decisão" onClose={onClose}>
      <div className="space-y-4">
        {!initialProjectId && (
          <div>
            <label htmlFor="table-project" className="block text-sm text-gray-300 mb-1.5">
              Projeto
            </label>
            {loadingProjects ? (
              <p className="text-sm text-gray-500">Carregando projetos...</p>
            ) : (
              <select
                id="table-project"
                value={selectedProjectId}
                onChange={(e) => { setSelectedProjectId(e.target.value); setError(null); }}
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
          <label htmlFor="table-gce" className="block text-sm text-gray-300 mb-1.5">
            GCE
          </label>
          {loadingGces ? (
            <p className="text-sm text-gray-500">Carregando GCEs...</p>
          ) : (
            <select
              id="table-gce"
              value={selectedGceId}
              onChange={(e) => { setSelectedGceId(e.target.value); setError(null); }}
              disabled={!selectedProjectId}
              className="w-full bg-surface border border-edge rounded-lg px-4 py-2.5 text-gray-100 focus:border-primary focus:outline-none transition-colors disabled:opacity-50"
            >
              <option value="">
                {!selectedProjectId
                  ? 'Selecione um projeto primeiro...'
                  : gces.length === 0
                    ? 'Nenhum GCE disponível'
                    : 'Selecione um GCE...'}
              </option>
              {gces.map((g) => (
                <option key={g.id} value={g.id}>{g.name}</option>
              ))}
            </select>
          )}
          {selectedProjectId && !loadingGces && gces.length === 0 && (
            <p className="text-xs text-gray-500 mt-1.5">
              Este projeto não possui GCEs. Crie um GCE antes de gerar uma tabela.
            </p>
          )}
        </div>

        {error && <p className="text-sm text-red-400">{error}</p>}

        <div className="flex gap-3 pt-2">
          <Button type="button" variant="ghost" className="flex-1 justify-center" onClick={onClose}>
            Cancelar
          </Button>
          <Button
            type="button"
            className="flex-1 justify-center"
            disabled={!selectedProjectId || !selectedGceId}
            onClick={handleOpen}
          >
            Abrir Tabela
          </Button>
        </div>
      </div>
    </Modal>
  );
}
