import { X, AlertCircle, CheckCircle, AlertTriangle } from 'lucide-react';
import type { GCEValidationResponse } from '../types/gce';

interface ValidationPanelProps {
  result: GCEValidationResponse | null;
  onClose: () => void;
}

export function ValidationPanel({ result, onClose }: ValidationPanelProps) {
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

      <div className="p-4 max-h-[70vh] overflow-y-auto space-y-3">
        {!result ? (
          <p className="text-sm text-gray-400">Validando...</p>
        ) : result.valid && result.errors.length === 0 && result.warnings.length === 0 ? (
          <div className="text-sm text-green-400 flex items-center gap-2">
            <CheckCircle className="w-4 h-4 shrink-0" />
            GCE validado com sucesso! Nenhum problema encontrado.
          </div>
        ) : (
          <>
            {result.errors.length > 0 && (
              <div className="space-y-2">
                <p className="text-xs text-gray-500 uppercase tracking-wide">
                  {result.errors.length} {result.errors.length === 1 ? 'erro' : 'erros'}
                </p>
                {result.errors.map((err) => (
                  <div
                    key={err.code}
                    className="flex items-start gap-2 bg-surface border border-red-500/30 rounded p-3"
                  >
                    <AlertCircle className="w-4 h-4 text-red-400 shrink-0 mt-0.5" />
                    <div>
                      <p className="text-xs text-gray-500 font-mono">{err.code}</p>
                      <p className="text-sm text-gray-300 mt-0.5">{err.message}</p>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {result.warnings.length > 0 && (
              <div className="space-y-2">
                <p className="text-xs text-gray-500 uppercase tracking-wide">
                  {result.warnings.length} {result.warnings.length === 1 ? 'aviso' : 'avisos'}
                </p>
                {result.warnings.map((warn) => (
                  <div
                    key={warn.code}
                    className="flex items-start gap-2 bg-surface border border-yellow-500/30 rounded p-3"
                  >
                    <AlertTriangle className="w-4 h-4 text-yellow-400 shrink-0 mt-0.5" />
                    <div>
                      <p className="text-xs text-gray-500 font-mono">{warn.code}</p>
                      <p className="text-sm text-gray-300 mt-0.5">{warn.message}</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
