import { useState } from 'react';
import { X, AlertTriangle } from 'lucide-react';
import { Button } from '../../../components/Button';

interface DeleteProjectModalProps {
  projectName: string;
  onClose: () => void;
  onConfirm: () => Promise<void>;
}

export function DeleteProjectModal({ projectName, onClose, onConfirm }: DeleteProjectModalProps) {
  const [loading, setLoading] = useState(false);

  const handleConfirm = async () => {
    setLoading(true);
    await onConfirm();
    setLoading(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/60" onClick={onClose} />

      <div className="relative bg-surface-elevated border border-edge rounded-lg w-full max-w-sm mx-4 p-6">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-1 hover:bg-surface-hover rounded transition-colors"
        >
          <X className="w-5 h-5 text-gray-400" />
        </button>

        <div className="flex flex-col items-center text-center">
          <div className="p-3 bg-red-500/10 rounded-full mb-4">
            <AlertTriangle className="w-6 h-6 text-red-400" />
          </div>

          <h2 className="text-lg font-semibold text-gray-100 mb-2">Excluir projeto</h2>
          <p className="text-sm text-gray-400 mb-6">
            Tem certeza que deseja excluir o projeto <span className="text-gray-200 font-medium">{projectName}</span>? Essa ação não pode ser desfeita.
          </p>

          <div className="flex gap-3 w-full">
            <Button
              type="button"
              variant="ghost"
              className="flex-1 justify-center"
              onClick={onClose}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              variant="danger-filled"
              disabled={loading}
              className="flex-1 justify-center"
              onClick={handleConfirm}
            >
              {loading ? 'Excluindo...' : 'Excluir'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
