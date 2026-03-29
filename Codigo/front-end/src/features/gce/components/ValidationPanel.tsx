import { X, AlertCircle } from 'lucide-react';
import type { GCEValidationError } from '../types/gce';

interface ValidationPanelProps {
  errors: GCEValidationError[];
  onClose: () => void;
  onSelectError: (elementId: string) => void;
}

export function ValidationPanel({ errors, onClose, onSelectError }: ValidationPanelProps) {
  return (
    <div className="absolute top-16 right-4 w-96 bg-surface-card border border-edge rounded-lg shadow-xl z-50">
      <div className="flex items-center justify-between p-4 border-b border-edge">
        <div className="flex items-center gap-2">
          <AlertCircle className="w-5 h-5 text-red-400" />
          <h3 className="text-sm text-gray-300">Validacao do GCE</h3>
        </div>
        <button onClick={onClose} className="text-gray-500 hover:text-white transition-colors">
          <X className="w-5 h-5" />
        </button>
      </div>

      <div className="p-4">
        {errors.length === 0 ? (
          <div className="text-sm text-green-400 flex items-center gap-2">
            <div className="w-2 h-2 bg-green-400 rounded-full" />
            GCE validado com sucesso! Nenhum erro encontrado.
          </div>
        ) : (
          <div className="space-y-3">
            <p className="text-sm text-gray-500">
              {errors.length} {errors.length === 1 ? 'erro encontrado' : 'erros encontrados'}
            </p>

            <div className="space-y-2 max-h-96 overflow-y-auto">
              {errors.map((error) => (
                <button
                  key={error.id}
                  className="w-full text-left bg-surface border border-edge rounded p-3 hover:border-primary transition-colors"
                  onClick={() => {
                    onSelectError(error.elementId);
                    onClose();
                  }}
                >
                  <div className="flex items-start gap-2">
                    <AlertCircle className="w-4 h-4 text-red-400 shrink-0 mt-0.5" />
                    <div>
                      <p className="text-sm text-gray-300">{error.message}</p>
                      <p className="text-xs text-gray-500 mt-1">
                        {error.elementType === 'node' ? 'No' : 'Aresta'}: {error.elementId}
                      </p>
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}
      </div>

      {errors.length > 0 && (
        <div className="p-4 border-t border-edge bg-surface rounded-b-lg">
          <p className="text-xs text-gray-600">
            Clique em um erro para destacar o elemento no grafo
          </p>
        </div>
      )}
    </div>
  );
}
