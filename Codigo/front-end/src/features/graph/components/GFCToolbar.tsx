import { Trash2, FileCode, Code2, FlaskConical, LayoutGrid, Loader2 } from 'lucide-react';
import { Button } from '../../../components/Button';

interface GFCToolbarProps {
  gfcName: string;
  methodSignature: string | null;
  onDelete?: () => void;
  canDelete?: boolean;
  onViewSource?: () => void;
  canViewSource?: boolean;
  onViewMethod?: () => void;
  canViewMethod?: boolean;
  onRelayout?: () => void;
  relayoutLoading?: boolean;
  onGenerateTests?: () => void;
  generateTestsLoading?: boolean;
}

export function GFCToolbar({
  gfcName,
  methodSignature,
  onDelete,
  canDelete = false,
  onViewSource,
  canViewSource = false,
  onViewMethod,
  canViewMethod = false,
  onRelayout,
  relayoutLoading = false,
  onGenerateTests,
  generateTestsLoading = false,
}: GFCToolbarProps) {
  return (
    <div className="h-12 bg-surface-card border-b border-edge flex items-center justify-between px-4 shrink-0">
      <div className="flex items-center gap-3 min-w-0 mr-4">
        <span className="text-sm font-medium text-gray-200 truncate" title={gfcName}>
          {gfcName}
        </span>
        {methodSignature && (
          <span
            className="text-xs font-mono text-gray-500 truncate hidden sm:inline"
            title={methodSignature}
          >
            {methodSignature}
          </span>
        )}
      </div>

      <div className="flex items-center gap-2 shrink-0">
        {onGenerateTests && (
          <Button
            size="sm"
            variant="outline"
            onClick={onGenerateTests}
            disabled={generateTestsLoading}
            title="Gera assinaturas de teste estrutural (uma por caminho independente)"
          >
            {generateTestsLoading ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <FlaskConical className="w-4 h-4" />
            )}
            Gerar testes
          </Button>
        )}
        {onRelayout && (
          <Button
            size="sm"
            variant="outline"
            onClick={onRelayout}
            disabled={relayoutLoading}
            title="Recalcula a posição dos nós e descarta o layout manual salvo"
          >
            {relayoutLoading ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <LayoutGrid className="w-4 h-4" />
            )}
            Reorganizar
          </Button>
        )}
        {canViewMethod && onViewMethod && (
          <Button size="sm" variant="outline" onClick={onViewMethod}>
            <Code2 className="w-4 h-4" />
            Ver método
          </Button>
        )}
        {canViewSource && onViewSource && (
          <Button size="sm" variant="outline" onClick={onViewSource}>
            <FileCode className="w-4 h-4" />
            Ver código
          </Button>
        )}
        {canDelete && onDelete && (
          <Button size="sm" variant="danger" onClick={onDelete}>
            <Trash2 className="w-4 h-4" />
            Excluir
          </Button>
        )}
      </div>
    </div>
  );
}
