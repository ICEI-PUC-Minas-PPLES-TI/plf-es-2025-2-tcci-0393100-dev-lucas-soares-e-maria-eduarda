import { useEffect, useMemo, useRef, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import { EditorView } from '@codemirror/view';
import { ChevronLeft, ChevronRight, Code2 } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';
import SourceFileService from '../../../services/GFC/SourceFileService';
import { highlightField, setHighlightEffect } from '../utils/cmHighlight';
import type { GFCSourceMethodCodeDTO } from '../types/gfc';

type ViewMode = 'method' | 'file';

interface MethodCodePanelProps {
  sourceFileId: string | null;
  methodSignature: string | null;
  fileName?: string | null;
  isCollapsed: boolean;
  onToggleCollapse: () => void;
  selectedStartLine?: number | null;
  selectedEndLine?: number | null;
}

export function MethodCodePanel({
  sourceFileId,
  methodSignature,
  fileName,
  isCollapsed,
  onToggleCollapse,
  selectedStartLine,
  selectedEndLine,
}: MethodCodePanelProps) {
  const { theme } = useTheme();
  const [viewMode, setViewMode] = useState<ViewMode>('method');
  const [method, setMethod] = useState<GFCSourceMethodCodeDTO | null>(null);
  const [fileCode, setFileCode] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loadedKey, setLoadedKey] = useState<string | null>(null);
  const viewRef = useRef<EditorView | null>(null);

  const fetchKey: string | null =
    isCollapsed || !sourceFileId
      ? null
      : viewMode === 'method'
        ? (methodSignature ? `m::${sourceFileId}::${methodSignature}` : null)
        : `f::${sourceFileId}`;

  useEffect(() => {
    if (!fetchKey || !sourceFileId) return;
    let cancelled = false;

    if (viewMode === 'method' && methodSignature) {
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
    } else if (viewMode === 'file') {
      SourceFileService.obterCodigoFonte(sourceFileId)
        .then((code) => {
          if (cancelled) return;
          setFileCode(code);
          setError(null);
          setLoadedKey(fetchKey);
        })
        .catch(() => {
          if (cancelled) return;
          setFileCode(null);
          setError('Erro ao carregar o arquivo.');
          setLoadedKey(fetchKey);
        });
    }

    return () => {
      cancelled = true;
    };
  }, [fetchKey, viewMode, sourceFileId, methodSignature]);

  const isStale = fetchKey !== null && fetchKey !== loadedKey;
  const displayMethod = !isStale && viewMode === 'method' ? method : null;
  const displayFile = !isStale && viewMode === 'file' ? fileCode : null;
  const displayCode = displayMethod?.sourceCode ?? displayFile;
  const displayError = isStale ? null : error;
  const loading = isStale;

  useEffect(() => {
    const view = viewRef.current;
    if (!view) return;

    if (selectedStartLine == null) {
      view.dispatch({ effects: setHighlightEffect.of(null) });
      return;
    }

    const docLines = view.state.doc.lines;
    let relStart: number;
    let relEnd: number;

    if (viewMode === 'method') {
      if (!displayMethod) return;
      let leadingOffset = 0;
      for (let i = 1; i <= docLines; i++) {
        if (view.state.doc.line(i).text.includes(displayMethod.name)) {
          leadingOffset = i - 1;
          break;
        }
      }
      const sourceStartFileLine = displayMethod.startLine - leadingOffset;
      relStart = selectedStartLine - sourceStartFileLine + 1;
      relEnd = (selectedEndLine ?? selectedStartLine) - sourceStartFileLine + 1;
    } else {
      if (displayFile == null) return;
      relStart = selectedStartLine;
      relEnd = selectedEndLine ?? selectedStartLine;
    }

    if (relStart < 1 || relStart > docLines) {
      view.dispatch({ effects: setHighlightEffect.of(null) });
      return;
    }

    const safeEnd = Math.min(docLines, Math.max(relStart, relEnd));
    view.dispatch({
      effects: [
        setHighlightEffect.of({ startLine: relStart, endLine: safeEnd }),
        EditorView.scrollIntoView(view.state.doc.line(relStart).from, { y: 'center' }),
      ],
    });
  }, [viewMode, displayMethod, displayFile, selectedStartLine, selectedEndLine]);

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
      <div
        role="button"
        tabIndex={0}
        onClick={onToggleCollapse}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            onToggleCollapse();
          }
        }}
        title="Expandir código"
        className="w-10 bg-surface-card border-l border-edge flex flex-col items-center pt-3 gap-2 shrink-0 cursor-pointer hover:bg-surface-hover transition-colors"
      >
        <ChevronLeft className="w-4 h-4 text-gray-400" />
        <Code2 className="w-4 h-4 text-gray-500" aria-hidden />
        <span className="[writing-mode:vertical-rl] rotate-180 text-[10px] tracking-wider uppercase text-gray-500 mt-1">
          Código
        </span>
      </div>
    );
  }

  const canViewMethod = !!methodSignature;

  const headerTitle = viewMode === 'method'
    ? (displayMethod?.name ?? 'Código do método')
    : (fileName ?? 'Arquivo');

  const headerSubtitle = viewMode === 'method' && displayMethod
    ? `linhas ${displayMethod.startLine}–${displayMethod.endLine}`
    : viewMode === 'file' && displayFile != null
      ? 'arquivo completo'
      : null;

  return (
    <div className="w-96 bg-surface-card border-l border-edge flex flex-col shrink-0">
      <div className="p-3 border-b border-edge flex items-start justify-between gap-2">
        <div className="flex items-center gap-2 min-w-0 flex-1">
          <Code2 className="w-4 h-4 text-primary-light shrink-0" />
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2">
              <ViewModeToggle
                value={viewMode}
                onChange={setViewMode}
                canViewMethod={canViewMethod}
              />
            </div>
            <h2 className="text-sm text-gray-200 font-medium truncate mt-1.5" title={headerTitle}>
              {headerTitle}
            </h2>
            {headerSubtitle && (
              <p className="text-xs text-gray-500 truncate">{headerSubtitle}</p>
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
        {!sourceFileId ? (
          <div className="h-full flex items-center justify-center p-4">
            <p className="text-sm text-gray-500 text-center">
              Nenhum arquivo associado a este GFC.
            </p>
          </div>
        ) : viewMode === 'method' && !methodSignature ? (
          <div className="h-full flex items-center justify-center p-4">
            <p className="text-sm text-gray-500 text-center">
              Nenhum método associado a este GFC.
            </p>
          </div>
        ) : displayError ? (
          <div className="h-full flex items-center justify-center p-4">
            <p className="text-sm text-red-400 text-center">{displayError}</p>
          </div>
        ) : loading || displayCode == null ? (
          <div className="h-full flex items-center justify-center">
            <p className="text-gray-400 text-sm">Carregando código...</p>
          </div>
        ) : (
          <CodeMirror
            value={displayCode}
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

function ViewModeToggle({
  value,
  onChange,
  canViewMethod,
}: {
  value: ViewMode;
  onChange: (mode: ViewMode) => void;
  canViewMethod: boolean;
}) {
  const baseClass = 'px-2 py-0.5 rounded-sm text-[10px] font-medium transition-colors';
  return (
    <div className="inline-flex bg-surface border border-edge rounded p-0.5">
      <button
        type="button"
        onClick={() => onChange('method')}
        disabled={!canViewMethod}
        className={`${baseClass} ${
          value === 'method'
            ? 'bg-surface-hover text-gray-200'
            : 'text-gray-500 hover:text-gray-300 disabled:opacity-40 disabled:hover:text-gray-500'
        }`}
      >
        Método
      </button>
      <button
        type="button"
        onClick={() => onChange('file')}
        className={`${baseClass} ${
          value === 'file'
            ? 'bg-surface-hover text-gray-200'
            : 'text-gray-500 hover:text-gray-300'
        }`}
      >
        Arquivo
      </button>
    </div>
  );
}
