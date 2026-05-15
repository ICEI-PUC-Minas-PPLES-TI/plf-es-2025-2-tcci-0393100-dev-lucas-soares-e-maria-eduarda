import { useEffect, useMemo, useRef, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import { EditorView } from '@codemirror/view';
import { ChevronLeft, ChevronRight, Code2 } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';
import SourceFileService from '../../../services/GFC/SourceFileService';
import { highlightField, setHighlightEffect } from '../utils/cmHighlight';
import type { GFCSourceMethodCodeDTO } from '../types/gfc';

interface MethodCodePanelProps {
  sourceFileId: string | null;
  methodSignature: string | null;
  isCollapsed: boolean;
  onToggleCollapse: () => void;
  selectedStartLine?: number | null;
  selectedEndLine?: number | null;
}

export function MethodCodePanel({
  sourceFileId,
  methodSignature,
  isCollapsed,
  onToggleCollapse,
  selectedStartLine,
  selectedEndLine,
}: MethodCodePanelProps) {
  const { theme } = useTheme();
  const [method, setMethod] = useState<GFCSourceMethodCodeDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loadedKey, setLoadedKey] = useState<string | null>(null);
  const viewRef = useRef<EditorView | null>(null);

  const fetchKey =
    !isCollapsed && sourceFileId && methodSignature
      ? `${sourceFileId}::${methodSignature}`
      : null;

  useEffect(() => {
    if (!fetchKey || !sourceFileId || !methodSignature) return;
    let cancelled = false;

    SourceFileService.obterMetodo(sourceFileId, methodSignature)
      .then((data) => {
        if (cancelled) return;
        setMethod(data);
        setError(null);
        setLoadedKey(fetchKey);
      })
      .catch(() => {
        if (cancelled) return;
        setMethod(null);
        setError('Erro ao carregar o código do método.');
        setLoadedKey(fetchKey);
      });

    return () => {
      cancelled = true;
    };
  }, [fetchKey, sourceFileId, methodSignature]);

  const isStale = fetchKey !== null && fetchKey !== loadedKey;
  const displayMethod = isStale ? null : method;
  const displayError = isStale ? null : error;
  const loading = isStale;

  // Converte linhas absolutas do arquivo para linhas relativas ao método exibido,
  // dispara o destaque e rola até a primeira linha selecionada.
  useEffect(() => {
    const view = viewRef.current;
    if (!view || !displayMethod) return;

    if (selectedStartLine == null) {
      view.dispatch({ effects: setHighlightEffect.of(null) });
      return;
    }

    const relStart = selectedStartLine - displayMethod.startLine + 1;
    const relEnd = (selectedEndLine ?? selectedStartLine) - displayMethod.startLine + 1;
    if (relStart < 1 || relStart > view.state.doc.lines) {
      view.dispatch({ effects: setHighlightEffect.of(null) });
      return;
    }

    const safeEnd = Math.min(view.state.doc.lines, Math.max(relStart, relEnd));
    view.dispatch({
      effects: [
        setHighlightEffect.of({ startLine: relStart, endLine: safeEnd }),
        EditorView.scrollIntoView(view.state.doc.line(relStart).from, { y: 'center' }),
      ],
    });
  }, [displayMethod, selectedStartLine, selectedEndLine]);

  const extensions = useMemo(
    () => [
      java(),
      EditorView.editable.of(false),
      EditorView.contentAttributes.of({ tabindex: '0' }),
      EditorView.lineWrapping,
      highlightField,
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
              {displayMethod?.name ?? 'Código do método'}
            </h2>
            {displayMethod && (
              <p className="text-xs text-gray-500 truncate">
                linhas {displayMethod.startLine}–{displayMethod.endLine}
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
        ) : displayError ? (
          <div className="h-full flex items-center justify-center p-4">
            <p className="text-sm text-red-400 text-center">{displayError}</p>
          </div>
        ) : loading || displayMethod == null ? (
          <div className="h-full flex items-center justify-center">
            <p className="text-gray-400 text-sm">Carregando código...</p>
          </div>
        ) : (
          <CodeMirror
            value={displayMethod.sourceCode}
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
            onCreateEditor={(view) => {
              viewRef.current = view;
            }}
          />
        )}
      </div>
    </div>
  );
}
