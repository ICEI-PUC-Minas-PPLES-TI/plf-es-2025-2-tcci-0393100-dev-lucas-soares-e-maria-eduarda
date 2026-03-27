import { Settings, Trash2 } from 'lucide-react';
import { Button } from '../../../components/Button';

interface ProjectHeaderProps {
  projectName: string;
  projectDescription: string;
}

export function ProjectHeader({ projectName, projectDescription }: ProjectHeaderProps) {
  return (
    <div className="bg-surface border-b border-edge">
      <div className="px-6 py-4 flex items-center justify-between">
        <div className="flex-1">
          <h1 className="text-2xl mb-1">{projectName} — Projeto</h1>
          <p className="text-gray-400 text-sm">{projectDescription}</p>
        </div>

        <div className="flex items-center gap-3">
          <Button variant="outline">
            <Settings className="w-4 h-4" />
            Editar Projeto
          </Button>

          <Button variant="danger">
            <Trash2 className="w-4 h-4" />
            Excluir Projeto
          </Button>
        </div>
      </div>
    </div>
  );
}
