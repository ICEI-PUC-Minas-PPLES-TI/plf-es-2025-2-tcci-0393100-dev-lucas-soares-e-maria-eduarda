import { useEffect, useMemo, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import { EditorView } from '@codemirror/view';
import { ChevronLeft, ChevronRight, Code2 } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';
import SourceFileService from '../../../services/GFC/SourceFileService';
import type { GFCSourceMethodCodeDTO } from '../types/gfc';

interface MethodCodePanelProps {
  sourceFileId: string | null;
  methodSignature: string | null;
  isCollapsed: boolean;
  onToggleCollapse: () => void;
}

export function MethodCodePanel({
  sourceFileId,
  methodSignature,
  isCollapsed,
  onToggleCollapse,
}: MethodCodePanelProps) {
  const { theme } = useTheme();
  const [method, setMethod] = useState<GFCSourceMethodCodeDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isCollapsed || !sourceFileId || !methodSignature) return;
    let cancelled = false;
    setLoading(true);
    setMethod(null);
    setError(null);

    SourceFileService.obterMetodo(sourceFileId, methodSignature)
      .then((data) => {
        if (!cancelled) setMethod(data);
      })
      .catch(() => {
        if (!cancelled) setError('Erro ao carregar o código do método.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [sourceFileId, methodSignature, isCollapsed]);

  const extensions = useMemo(
    () => [
      java(),
      EditorView.editable.of(false),
      EditorView.contentAttributes.of({ tabindex: '0' }),
      EditorView.lineWrapping,
    ],
    [],
  );

  if (isCollapsed) {
    return (
      <div className="w-10 bg-surface-card border-l border-edge flex items-start justify-center pt-3 shrink-0">
        <button
          onClick={onToggleCollapse}
          className="p-1.5 text-gray-400 hover:text-gray-200 hover:bg-surface-hover rounded transition-colors"
          title="Expandir código do método"
        >
          <ChevronLeft className="w-4 h-4" />
        </button>
      </div>
    );
  }

  return (
    <div className="w-96 bg-surface-card border-l border-edge flex flex-col shrink-0">
      <div className="p-3 border-b border-edge flex items-center justify-between gap-2">
        <div className="flex items-center gap-2 min-w-0">
          <Code2 className="w-4 h-4 text-primary-light shrink-0" />
          <div className="min-w-0">
            <h2 className="text-sm text-gray-200 font-medium truncate">
              {method?.name ?? 'Código do método'}
            </h2>
            {method && (
              <p className="text-xs text-gray-500 truncate">
                linhas {method.startLine}–{method.endLine}
              </p>
            )}
          </div>
        </div>
        <button
          onClick={onToggleCollapse}
          className="p-1.5 text-gray-400 hover:text-gray-200 hover:bg-surface-hover rounded transition-colors shrink-0"
          title="Recolher painel"
        >
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>

      <div className="flex-1 overflow-hidden bg-surface">
        {!sourceFileId || !methodSignature ? (
          <div className="h-full flex items-center justify-center p-4">
            <p className="text-sm text-gray-500 text-center">
              Nenhum método associado a este GFC.
            </p>
          </div>
        ) : error ? (
          <div className="h-full flex items-center justify-center p-4">
            <p className="text-sm text-red-400 text-center">{error}</p>
          </div>
        ) : loading || method == null ? (
          <div className="h-full flex items-center justify-center">
            <p className="text-gray-400 text-sm">Carregando código...</p>
          </div>
        ) : (
          <CodeMirror
            value={method.sourceCode}
            extensions={extensions}
            editable={false}
            readOnly
            theme={theme === 'light' ? 'light' : 'dark'}
            basicSetup={{
              lineNumbers: true,
              highlightActiveLine: false,
              highlightActiveLineGutter: false,
              foldGutter: true,
              allowMultipleSelections: false,
              searchKeymap: true,
            }}
            height="100%"
            className="h-full text-xs"
          />
        )}
      </div>
    </div>
  );
}
