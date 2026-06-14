import { useEffect, useMemo, useRef, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import { EditorView, Decoration, WidgetType, type DecorationSet } from '@codemirror/view';
import { RangeSetBuilder, type Text } from '@codemirror/state';
import { X, FileCode } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';
import SourceFileService from '../../../services/GFC/SourceFileService';
import { highlightField, setHighlightEffect } from '../utils/cmHighlight';
import type {
  GFCSourceFileDTO,
  GFCSourceMethodDTO,
  GFCSummaryDTO,
} from '../types/gfc';

interface SourceFileViewerModalProps {
  sourceFile: GFCSourceFileDTO;
  methods: GFCSourceMethodDTO[];
  existingGfcs: GFCSummaryDTO[];
  generatingSignature: string | null;
  onClose: () => void;
  onGenerate: (method: GFCSourceMethodDTO) => void;
  onOpenGfc: (gfcId: string) => void;
  initialHighlight?: { startLine: number; endLine: number } | null;
}

type MethodActionIcon = 'play' | 'open' | 'spinner';

const ICON_PATHS: Record<MethodActionIcon, string> = {
  play: '<polygon points="6 3 20 12 6 21 6 3" fill="currentColor" stroke="none" />',
  open: '<path d="M5 12h14" /><path d="M13 5l7 7-7 7" />',
  spinner: '<path d="M21 12a9 9 0 1 1-6.219-8.56" />',
};

class MethodActionWidget extends WidgetType {
  readonly label: string;
  readonly icon: MethodActionIcon;
  readonly disabled: boolean;
  readonly onClick: () => void;

  constructor(label: string, icon: MethodActionIcon, disabled: boolean, onClick: () => void) {
    super();
    this.label = label;
    this.icon = icon;
    this.disabled = disabled;
    this.onClick = onClick;
  }

  eq(other: MethodActionWidget) {
    return (
      other.label === this.label &&
      other.icon === this.icon &&
      other.disabled === this.disabled
    );
  }

  toDOM() {
    const btn = document.createElement('button');
    btn.className = 'gfc-inline-action';
    btn.type = 'button';
    btn.disabled = this.disabled;
    btn.innerHTML = `
      <svg class="gfc-inline-action__icon${this.icon === 'spinner' ? ' gfc-inline-action__icon--spin' : ''}" viewBox="0 0 24 24" width="11" height="11" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        ${ICON_PATHS[this.icon]}
      </svg>
      <span>${this.label}</span>
    `;
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      if (!this.disabled) this.onClick();
    });
    btn.addEventListener('mousedown', (e) => e.preventDefault());
    return btn;
  }

  ignoreEvent() {
    return false;
  }
}

function buildMethodDecorations(
  doc: Text,
  methods: GFCSourceMethodDTO[],
  existingGfcs: GFCSummaryDTO[],
  generatingSignature: string | null,
  onGenerate: (m: GFCSourceMethodDTO) => void,
  onOpenGfc: (id: string) => void,
): DecorationSet {
  const builder = new RangeSetBuilder<Decoration>();
  const sorted = [...methods].sort((a, b) => a.startLine - b.startLine);

  for (const method of sorted) {
    if (method.startLine < 1 || method.startLine > doc.lines) continue;
    const line = doc.line(method.startLine);
    const existing = existingGfcs.find((g) => g.methodSignature === method.signature);
    const isGenerating = generatingSignature === method.signature;
    const label = isGenerating ? 'Gerando...' : existing ? 'Abrir GFC' : 'Gerar GFC';
    const icon: MethodActionIcon = isGenerating ? 'spinner' : existing ? 'open' : 'play';

    const widget = new MethodActionWidget(label, icon, isGenerating, () => {
      if (existing) onOpenGfc(existing.id);
      else onGenerate(method);
    });

    builder.add(line.to, line.to, Decoration.widget({ widget, side: 1 }));
  }

  return builder.finish();
}

export function SourceFileViewerModal({
  sourceFile,
  methods,
  existingGfcs,
  generatingSignature,
  onClose,
  onGenerate,
  onOpenGfc,
  initialHighlight,
}: SourceFileViewerModalProps) {
  const { theme } = useTheme();
  const [loadedFileId, setLoadedFileId] = useState<string | null>(null);
  const [code, setCode] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const viewRef = useRef<EditorView | null>(null);

  useEffect(() => {
    let cancelled = false;
    SourceFileService.obterCodigoFonte(sourceFile.projectId, sourceFile.id)
      .then((src) => {
        if (cancelled) return;
        setCode(src);
        setError(null);
        setLoadedFileId(sourceFile.id);
      })
      .catch(() => {
        if (cancelled) return;
        setCode(null);
        setError('Erro ao carregar o código-fonte.');
        setLoadedFileId(sourceFile.id);
      });

    return () => {
      cancelled = true;
    };
  }, [sourceFile.projectId, sourceFile.id]);

  const isStale = loadedFileId !== sourceFile.id;
  const displayCode = isStale ? null : code;
  const displayError = isStale ? null : error;

  const extensions = useMemo(() => {
    const decorationField = EditorView.decorations.compute([], (state) =>
      buildMethodDecorations(
        state.doc,
        methods,
        existingGfcs,
        generatingSignature,
        onGenerate,
        onOpenGfc,
      ),
    );

    return [
      java(),
      EditorView.editable.of(false),
      EditorView.contentAttributes.of({ tabindex: '0' }),
      EditorView.lineWrapping,
      decorationField,
      highlightField,
    ];
  }, [methods, existingGfcs, generatingSignature, onGenerate, onOpenGfc]);

  // Aplica destaque/scroll quando o código termina de carregar ou o range muda.
  useEffect(() => {
    const view = viewRef.current;
    if (!view || displayCode == null) return;

    if (!initialHighlight) {
      view.dispatch({ effects: setHighlightEffect.of(null) });
      return;
    }

    const doc = view.state.doc;
    const start = Math.max(1, initialHighlight.startLine);
    if (start > doc.lines) return;
    const end = Math.min(doc.lines, Math.max(start, initialHighlight.endLine));

    view.dispatch({
      effects: [
        setHighlightEffect.of({ startLine: start, endLine: end }),
        EditorView.scrollIntoView(doc.line(start).from, { y: 'center' }),
      ],
    });
  }, [displayCode, initialHighlight]);

  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [onClose]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-8">
      <div className="absolute inset-0 bg-black/70" onClick={onClose} />

      <div className="relative bg-surface-card border border-edge rounded-lg w-full h-full max-w-6xl flex flex-col overflow-hidden">
        <div className="flex items-center justify-between gap-3 px-4 py-3 border-b border-edge">
          <div className="flex items-center gap-2 min-w-0">
            <FileCode className="w-4 h-4 text-primary-light shrink-0" />
            <div className="min-w-0">
              <p className="text-sm font-mono text-gray-200 truncate" title={sourceFile.fileName}>
                {sourceFile.fileName}
              </p>
              <p className="text-xs text-gray-500">
                {sourceFile.language} · {methods.length} método{methods.length !== 1 ? 's' : ''} · passe o mouse sobre a assinatura para gerar/abrir o GFC
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-gray-400 hover:text-gray-200 hover:bg-surface-hover rounded transition-colors shrink-0"
            title="Fechar"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex-1 overflow-hidden bg-surface">
          {displayError ? (
            <div className="h-full flex items-center justify-center">
              <p className="text-red-400 text-sm">{displayError}</p>
            </div>
          ) : displayCode == null ? (
            <div className="h-full flex items-center justify-center">
              <p className="text-gray-400 text-sm">Carregando código-fonte...</p>
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
              className="h-full text-sm"
              onCreateEditor={(view) => {
                viewRef.current = view;
              }}
            />
          )}
        </div>
      </div>
    </div>
  );
}
