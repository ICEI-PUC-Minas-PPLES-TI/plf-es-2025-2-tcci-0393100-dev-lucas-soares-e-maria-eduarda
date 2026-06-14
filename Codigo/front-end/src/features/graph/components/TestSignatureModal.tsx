import { useEffect, useMemo, useRef, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import { EditorView } from '@codemirror/view';
import { AlertTriangle, Check, Copy, FlaskConical, X } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';
import { highlightField, setHighlightEffect } from '../utils/cmHighlight';

export interface TestMethodItem {
  /** Nome único do método (usado pra localizar no source). */
  methodName: string;
  /** Rótulo opcional exibido na lista (ex.: ruleCode "R1"). */
  badge?: string;
}

interface TestSignatureModalProps {
  open: boolean;
  onClose: () => void;
  title: string;
  subtitle?: string | null;
  generatedCode: string | null;
  /** Quando true, mostra spinner em vez do código. */
  loading: boolean;
  error?: string | null;
  methods?: TestMethodItem[];
  warnings?: string[];
}

/**
 * Modal compartilhado entre GFC (testes estruturais) e Tabela de Decisão (testes
 * funcionais). Renderiza o `generatedCode` num CodeMirror Java read-only;
 * quando há vários métodos, exibe uma sidebar à esquerda com a lista — clicar
 * scrolla e destaca o método no editor.
 */
export function TestSignatureModal({
  open,
  onClose,
  title,
  subtitle,
  generatedCode,
  loading,
  error,
  methods,
  warnings,
}: TestSignatureModalProps) {
  const { theme } = useTheme();
  const viewRef = useRef<EditorView | null>(null);
  const [copied, setCopied] = useState(false);
  const [selectedMethod, setSelectedMethod] = useState<string | null>(null);

  // Derived state: ao mudar de "aberto" pra "fechado" reseta o estado interno
  // sem precisar de useEffect (que dispararia a regra `react-hooks/set-state-in-effect`).
  const [prevOpen, setPrevOpen] = useState(open);
  if (open !== prevOpen) {
    setPrevOpen(open);
    if (!open) {
      setCopied(false);
      setSelectedMethod(null);
    }
  }

  // ESC pra fechar.
  useEffect(() => {
    if (!open) return;
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [open, onClose]);

  // Aplica highlight quando um método é selecionado na sidebar.
  useEffect(() => {
    const view = viewRef.current;
    if (!view || generatedCode == null) return;

    if (!selectedMethod) {
      view.dispatch({ effects: setHighlightEffect.of(null) });
      return;
    }

    const doc = view.state.doc;
    let startLine = -1;
    let endLine = -1;
    // Heurística simples: encontra primeira linha que contém o nome do método;
    // o "fim" do método é deduzido procurando o próximo "void " ou final do arquivo.
    for (let i = 1; i <= doc.lines; i++) {
      const text = doc.line(i).text;
      if (startLine === -1 && text.includes(selectedMethod)) {
        startLine = i;
        continue;
      }
      if (startLine !== -1 && /^\s*(public|private|protected|@Test)/.test(text) && !text.includes(selectedMethod)) {
        endLine = i - 1;
        break;
      }
    }
    if (startLine === -1) return;
    if (endLine === -1) endLine = doc.lines;

    view.dispatch({
      effects: [
        setHighlightEffect.of({ startLine, endLine }),
        EditorView.scrollIntoView(doc.line(startLine).from, { y: 'start' }),
      ],
    });
  }, [selectedMethod, generatedCode]);

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

  const handleCopy = async () => {
    if (!generatedCode) return;
    try {
      await navigator.clipboard.writeText(generatedCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 1800);
    } catch {
      // navegador sem clipboard API — silencioso.
    }
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-8">
      <div className="absolute inset-0 bg-black/70" onClick={onClose} />

      <div className="relative bg-surface-card border border-edge rounded-lg w-full h-full max-w-6xl flex flex-col overflow-hidden">
        <div className="flex items-start justify-between gap-3 px-4 py-3 border-b border-edge shrink-0">
          <div className="flex items-center gap-2 min-w-0">
            <FlaskConical className="w-4 h-4 text-primary-light shrink-0" />
            <div className="min-w-0">
              <p className="text-sm font-mono text-gray-200 truncate" title={title}>
                {title}
              </p>
              {subtitle && <p className="text-xs text-gray-500 truncate">{subtitle}</p>}
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

        {warnings && warnings.length > 0 && (
          <div className="px-4 py-2 border-b border-edge bg-yellow-500/5 shrink-0 space-y-1">
            {warnings.map((w, i) => (
              <div key={i} className="flex items-start gap-1.5 text-xs text-yellow-300/90">
                <AlertTriangle className="w-3.5 h-3.5 shrink-0 mt-0.5" />
                <span>{w}</span>
              </div>
            ))}
          </div>
        )}

        <div className="flex-1 flex overflow-hidden">
          {methods && methods.length > 1 && (
            <div className="w-56 bg-surface-card border-r border-edge flex flex-col shrink-0">
              <div className="px-3 py-2 border-b border-edge">
                <p className="text-xs text-gray-400 font-medium">
                  Métodos de teste{' '}
                  <span className="text-gray-600 font-mono">({methods.length})</span>
                </p>
              </div>
              <div className="flex-1 overflow-y-auto thin-scrollbar p-1.5 space-y-1">
                {methods.map((m) => {
                  const isSelected = selectedMethod === m.methodName;
                  return (
                    <button
                      key={m.methodName}
                      onClick={() => setSelectedMethod((cur) => (cur === m.methodName ? null : m.methodName))}
                      className={`w-full text-left px-2 py-1.5 rounded transition-colors flex items-center gap-1.5 ${
                        isSelected
                          ? 'bg-primary/15 border border-primary/40'
                          : 'hover:bg-surface-hover border border-transparent'
                      }`}
                    >
                      {m.badge && (
                        <span className="text-[9px] font-mono text-gray-500 shrink-0">{m.badge}</span>
                      )}
                      <span
                        className={`text-xs font-mono truncate ${isSelected ? 'text-primary-light' : 'text-gray-200'}`}
                        title={m.methodName}
                      >
                        {m.methodName}
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          <div className="flex-1 overflow-hidden bg-surface relative">
            {error ? (
              <div className="h-full flex items-center justify-center p-4">
                <p className="text-sm text-red-400 text-center">{error}</p>
              </div>
            ) : loading || generatedCode == null ? (
              <div className="h-full flex items-center justify-center">
                <p className="text-gray-400 text-sm">Gerando assinaturas de teste...</p>
              </div>
            ) : (
              <>
                <CodeMirror
                  value={generatedCode}
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
                <button
                  type="button"
                  onClick={handleCopy}
                  title={copied ? 'Código copiado' : 'Copiar código'}
                  aria-label={copied ? 'Código copiado' : 'Copiar código'}
                  className={`absolute top-2 right-3 z-10 inline-flex items-center gap-1 px-2 py-1 rounded border text-xs font-medium transition-colors ${
                    copied
                      ? 'bg-green-500/15 border-green-500/40 text-green-400'
                      : 'bg-surface-card/90 border-edge text-gray-300 hover:text-gray-100 hover:bg-surface-hover backdrop-blur-sm'
                  }`}
                >
                  {copied ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                  <span>{copied ? 'Copiado' : 'Copiar'}</span>
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
