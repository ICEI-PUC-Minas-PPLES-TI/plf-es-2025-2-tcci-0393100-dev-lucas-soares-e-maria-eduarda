import { ArrowLeft, RotateCw, Save, AlertTriangle, CheckCircle, FlaskConical, Loader2 } from 'lucide-react';
import { Button } from '../../../components/Button';

interface DecisionTableToolbarProps {
  tableName: string;
  gceName: string;
  saveStatus: 'idle' | 'saving' | 'saved' | 'error';
  syncStatus?: 'UP_TO_DATE' | 'STALE';
  regenerateStatus?: 'idle' | 'loading' | 'synced' | 'error';
  onBack: () => void;
  onRegenerate: () => void;
  onSave: () => void;
  onGenerateTests?: () => void;
  generateTestsLoading?: boolean;
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
  regenerateStatus = 'idle',
  onBack,
  onRegenerate,
  onSave,
  onGenerateTests,
  generateTestsLoading = false,
}: DecisionTableToolbarProps) {
  return (
    <div className="h-12 bg-surface-card border-b border-edge flex items-center justify-between px-4 shrink-0">
      <div className="flex items-center gap-2 min-w-0 mr-4">
        <button
          onClick={onBack}
          className="flex items-center gap-1.5 px-2 py-1 rounded text-gray-500 hover:text-gray-200 hover:bg-surface-hover transition-colors text-sm"
          title="Voltar ao GCE"
        >
          <ArrowLeft className="w-4 h-4 shrink-0" />
          <span>Ver GCE</span>
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
        {onGenerateTests && (
          <Button
            size="sm"
            variant="outline"
            onClick={onGenerateTests}
            disabled={generateTestsLoading}
            title="Gera assinaturas de teste funcional (uma por regra da tabela)"
          >
            {generateTestsLoading ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <FlaskConical className="w-4 h-4" />
            )}
            Gerar testes
          </Button>
        )}
        {regenerateStatus === 'synced' ? (
          <span className="flex items-center gap-1.5 px-2 py-1 text-sm text-green-400">
            <CheckCircle className="w-4 h-4 shrink-0" />
            Tabela já está atualizada
          </span>
        ) : (
          <Button size="sm" variant="ghost" onClick={onRegenerate} disabled={regenerateStatus === 'loading'}>
            <RotateCw className={`w-4 h-4 ${regenerateStatus === 'loading' ? 'animate-spin' : ''}`} />
            {regenerateStatus === 'loading' ? 'Atualizando...' : regenerateStatus === 'error' ? 'Erro ao atualizar' : 'Atualizar Tabela'}
          </Button>
        )}

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
