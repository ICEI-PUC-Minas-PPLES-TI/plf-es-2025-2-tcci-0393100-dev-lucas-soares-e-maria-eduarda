import { AlertCircle, CheckCircle } from 'lucide-react';

interface ValidationStatusBarProps {
  conditionCount: number;
  effectCount: number;
  ruleCount: number;
  generatedAt: string;
  updatedAt?: string;
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function ValidationStatusBar({
  conditionCount,
  effectCount,
  ruleCount,
  generatedAt,
  updatedAt,
}: ValidationStatusBarProps) {
  const isValid = conditionCount > 0 && effectCount > 0 && ruleCount > 0;

  const statusReason = conditionCount === 0
    ? 'GCE sem causas'
    : effectCount === 0
      ? 'GCE sem efeitos'
      : 'Nenhuma regra gerada';

  return (
    <div className="h-9 border-t border-edge bg-surface-card flex items-center justify-between px-5 shrink-0">
      <div className="flex items-center gap-5 text-xs text-gray-500">
        <span>
          Condições: <span className="text-gray-300">{conditionCount}</span>
        </span>
        <span>
          Efeitos: <span className="text-gray-300">{effectCount}</span>
        </span>
        <span>
          Regras: <span className="text-gray-300">{ruleCount}</span>
        </span>

        <span className="text-gray-700">|</span>

        <span>Gerada em: {formatDate(generatedAt)}</span>

        {updatedAt && (
          <span>Salva em: {formatDate(updatedAt)}</span>
        )}
      </div>

      <div className="flex items-center gap-1.5">
        {isValid ? (
          <>
            <CheckCircle className="w-3.5 h-3.5 text-node-cause" />
            <span className="text-xs text-node-cause">Tabela válida</span>
          </>
        ) : (
          <>
            <AlertCircle className="w-3.5 h-3.5 text-gray-500" />
            <span className="text-xs text-gray-500">{statusReason}</span>
          </>
        )}
      </div>
    </div>
  );
}
