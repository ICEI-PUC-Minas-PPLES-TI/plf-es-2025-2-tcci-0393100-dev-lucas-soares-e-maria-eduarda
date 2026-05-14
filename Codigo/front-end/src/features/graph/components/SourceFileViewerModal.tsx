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

class MethodActionWidget extends WidgetType {
  constructor(
    private readonly label: string,
    private readonly disabled: boolean,
    private readonly onClick: () => void,
  ) {
    super();
  }

  eq(other: MethodActionWidget) {
    return other.label === this.label && other.disabled === this.disabled;
  }

  toDOM() {
    const btn = document.createElement('button');
    btn.className = 'gfc-inline-action';
    btn.type = 'button';
    btn.disabled = this.disabled;
    btn.textContent = this.label;
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
    const label = isGenerating ? 'Gerando...' : existing ? '⚡ Abrir GFC' : '⚡ Gerar GFC';

    const widget = new MethodActionWidget(label, isGenerating, () => {
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
  const [code, setCode] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const viewRef = useRef<EditorView | null>(null);

  useEffect(() => {
    let cancelled = false;
    setCode(null);
    setError(null);

    SourceFileService.obterCodigoFonte(sourceFile.id)
      .then((src) => {
        if (!cancelled) setCode(src);
      })
      .catch(() => {
        if (!cancelled) setError('Erro ao carregar o código-fonte.');
      });

    return () => {
      cancelled = true;
    };
  }, [sourceFile.id]);

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
    if (!view || code == null) return;

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
  }, [code, initialHighlight]);

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
          {error ? (
            <div className="h-full flex items-center justify-center">
              <p className="text-red-400 text-sm">{error}</p>
            </div>
          ) : code == null ? (
            <div className="h-full flex items-center justify-center">
              <p className="text-gray-400 text-sm">Carregando código-fonte...</p>
            </div>
          ) : (
            <CodeMirror
              value={code}
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
