import { ArrowLeft, RotateCw, Save, AlertTriangle } from 'lucide-react';
import { Button } from '../../../components/Button';

interface DecisionTableToolbarProps {
  tableName: string;
  gceName: string;
  saveStatus: 'idle' | 'saving' | 'saved' | 'error';
  syncStatus?: 'UP_TO_DATE' | 'STALE';
  onBack: () => void;
  onRegenerate: () => void;
  onSave: () => void;
}

const SAVE_LABELS: Record<string, string> = {
  idle: 'Salvar',
  saving: 'Salvando...',
  saved: 'Salvo!',
  error: 'Erro ao salvar',
};

export function DecisionTableToolbar({
  tableName,
  gceName,
  saveStatus,
  syncStatus,
  onBack,
  onRegenerate,
  onSave,
}: DecisionTableToolbarProps) {
  return (
    <div className="h-12 bg-surface-card border-b border-edge flex items-center justify-between px-4 shrink-0">
      <div className="flex items-center gap-2 min-w-0 mr-4">
        <button
          onClick={onBack}
          className="p-1 rounded text-gray-500 hover:text-gray-200 hover:bg-surface-hover transition-colors"
          title="Voltar ao GCE"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>

        <div className="min-w-0">
          <span className="text-sm font-medium text-gray-200 truncate">{tableName}</span>
          <span className="text-xs text-gray-600 ml-2">GCE: {gceName}</span>
          {syncStatus === 'STALE' && (
            <span className="ml-2 inline-flex items-center gap-1 text-xs text-yellow-400">
              <AlertTriangle className="w-3 h-3" />
              Desatualizada
            </span>
          )}
        </div>
      </div>

      <div className="flex items-center gap-2 shrink-0">
        <Button size="sm" variant="ghost" onClick={onRegenerate}>
          <RotateCw className="w-4 h-4" />
          Regenerar
        </Button>

        <Button
          size="sm"
          variant="outline"
          onClick={onSave}
          disabled={saveStatus === 'saving'}
        >
          <Save className="w-4 h-4" />
          {SAVE_LABELS[saveStatus]}
        </Button>
      </div>
    </div>
  );
}
