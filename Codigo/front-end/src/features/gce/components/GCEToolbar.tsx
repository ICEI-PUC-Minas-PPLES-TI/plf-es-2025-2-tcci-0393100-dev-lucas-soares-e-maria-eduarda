import { useRef, useState, useEffect } from 'react';
import { Save, CheckCircle, Table, Pencil } from 'lucide-react';
import { Button } from '../../../components/Button';

interface GCEToolbarProps {
  gceName: string;
  onSave: () => void;
  onValidate: () => void;
  onGenerateTable: () => void;
  onNameChange?: (name: string) => void;
  saveStatus?: 'idle' | 'saving' | 'saved' | 'error';
  canValidate?: boolean;
  canSave?: boolean;
}

export function GCEToolbar({ gceName, onSave, onValidate, onGenerateTable, onNameChange, saveStatus = 'idle', canValidate = true, canSave = false }: GCEToolbarProps) {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(gceName);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setDraft(gceName);
  }, [gceName]);

  const startEdit = () => {
    setDraft(gceName);
    setEditing(true);
    setTimeout(() => inputRef.current?.select(), 0);
  };

  const confirm = () => {
    setEditing(false);
    const trimmed = draft.trim();
    if (trimmed && trimmed !== gceName) {
      onNameChange?.(trimmed);
    } else {
      setDraft(gceName);
    }
  };

  const cancel = () => {
    setEditing(false);
    setDraft(gceName);
  };

  return (
    <div className="h-12 bg-surface-card border-b border-edge flex items-center justify-between px-4 shrink-0">
      <div className="flex items-center gap-1.5 min-w-0 mr-4">
        {editing ? (
          <input
            ref={inputRef}
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            onBlur={confirm}
            onKeyDown={(e) => {
              if (e.key === 'Enter') confirm();
              if (e.key === 'Escape') cancel();
            }}
            className="text-sm font-medium bg-surface border border-primary rounded px-2 py-0.5 text-gray-200 outline-none min-w-0 w-64 max-w-xs"
            autoFocus
          />
        ) : (
          <button
            onClick={startEdit}
            className="group flex items-center gap-1.5 text-sm text-gray-200 font-medium truncate hover:text-white transition-colors"
            title="Clique para renomear"
          >
            <span className="truncate">{gceName}</span>
            <Pencil className="w-3 h-3 text-gray-600 group-hover:text-gray-400 shrink-0" />
          </button>
        )}
      </div>

      <div className="flex items-center gap-2 shrink-0">
        <Button size="sm" variant="primary" onClick={onGenerateTable}>
          <Table className="w-4 h-4" />
          Gerar Tabela
        </Button>
        <Button size="sm" variant="outline" onClick={onValidate} disabled={!canValidate}>
          <CheckCircle className="w-4 h-4" />
          Validar
        </Button>
        <Button size="sm" variant="outline" onClick={onSave} disabled={!canSave || saveStatus === 'saving'}>
          <Save className="w-4 h-4" />
          {saveStatus === 'saving' ? 'Salvando...' : saveStatus === 'saved' ? 'Salvo!' : saveStatus === 'error' ? 'Erro ao salvar' : 'Salvar'}
        </Button>
      </div>
    </div>
  );
}
