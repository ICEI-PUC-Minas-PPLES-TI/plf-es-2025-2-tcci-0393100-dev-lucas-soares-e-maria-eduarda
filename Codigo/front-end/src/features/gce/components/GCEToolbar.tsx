import { Save, CheckCircle, Table } from 'lucide-react';
import { Button } from '../../../components/Button';

interface GCEToolbarProps {
  gceName: string;
  onSave: () => void;
  onValidate: () => void;
  onGenerateTable: () => void;
}

export function GCEToolbar({ gceName, onSave, onValidate, onGenerateTable }: GCEToolbarProps) {
  return (
    <div className="h-12 bg-surface-card border-b border-edge flex items-center justify-between px-4 shrink-0">
      <span className="text-sm text-gray-200 font-medium truncate">{gceName}</span>

      <div className="flex items-center gap-2">
        <Button size="sm" variant="primary" onClick={onGenerateTable}>
          <Table className="w-4 h-4" />
          Gerar Tabela
        </Button>
        <Button size="sm" variant="outline" onClick={onValidate}>
          <CheckCircle className="w-4 h-4" />
          Validar
        </Button>
        <Button size="sm" variant="outline" onClick={onSave}>
          <Save className="w-4 h-4" />
          Salvar
        </Button>
      </div>
    </div>
  );
}
