import { Settings, Trash2 } from 'lucide-react';
import { Button } from '../../../components/Button';
import { formatShortDate, formatRelativeDate } from '../../../utils/formatDate';

interface ProjectHeaderProps {
  projectName: string;
  projectDescription: string;
  createdAt: string;
  updatedAt: string | null;
  onEdit?: () => void;
  onDelete?: () => void;
}

export function ProjectHeader({ projectName, projectDescription, createdAt, updatedAt, onEdit, onDelete }: ProjectHeaderProps) {
  return (
    <div className="container mx-auto px-6 py-4 flex items-center justify-between border-b border-edge">
        <div className="flex-1">
          <h1 className="text-2xl mb-1">{projectName} — Projeto</h1>
          <p className="text-gray-400 text-sm">{projectDescription}</p>
          <p className="text-xs text-gray-600 mt-1">
            Criado em {formatShortDate(createdAt)}
            {updatedAt && <> · Atualizado {formatRelativeDate(updatedAt)}</>}
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button variant="outline" onClick={onEdit}>
            <Settings className="w-4 h-4" />
            Editar Projeto
          </Button>

          <Button variant="danger" onClick={onDelete}>
            <Trash2 className="w-4 h-4" />
            Excluir Projeto
          </Button>
        </div>
    </div>
  );
}
