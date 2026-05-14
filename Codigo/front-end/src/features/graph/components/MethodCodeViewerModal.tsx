import { useEffect, useMemo, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import { EditorView } from '@codemirror/view';
import { X, FileCode } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';
import SourceFileService from '../../../services/GFC/SourceFileService';
import type { GFCSourceMethodCodeDTO } from '../types/gfc';

interface MethodCodeViewerModalProps {
  sourceFileId: string;
  methodSignature: string;
  fileName: string | null;
  onClose: () => void;
}

export function MethodCodeViewerModal({
  sourceFileId,
  methodSignature,
  fileName,
  onClose,
}: MethodCodeViewerModalProps) {
  const { theme } = useTheme();
  const [method, setMethod] = useState<GFCSourceMethodCodeDTO | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setMethod(null);
    setError(null);

    SourceFileService.obterMetodo(sourceFileId, methodSignature)
      .then((data) => {
        if (!cancelled) setMethod(data);
      })
      .catch(() => {
        if (!cancelled) setError('Erro ao carregar o código do método.');
      });

    return () => {
      cancelled = true;
    };
  }, [sourceFileId, methodSignature]);

  const extensions = useMemo(
    () => [
      java(),
      EditorView.editable.of(false),
      EditorView.contentAttributes.of({ tabindex: '0' }),
      EditorView.lineWrapping,
    ],
    [],
  );

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

      <div className="relative bg-surface-card border border-edge rounded-lg w-full h-full max-w-4xl flex flex-col overflow-hidden">
        <div className="flex items-center justify-between gap-3 px-4 py-3 border-b border-edge">
          <div className="flex items-center gap-2 min-w-0">
            <FileCode className="w-4 h-4 text-primary-light shrink-0" />
            <div className="min-w-0">
              <p className="text-sm font-mono text-gray-200 truncate" title={methodSignature}>
                {method?.name ?? methodSignature}
              </p>
              <p className="text-xs text-gray-500 truncate">
                {fileName ?? 'Método selecionado'}
                {method && ` · linhas ${method.startLine}–${method.endLine}`}
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
          ) : method == null ? (
            <div className="h-full flex items-center justify-center">
              <p className="text-gray-400 text-sm">Carregando código do método...</p>
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
              className="h-full text-sm"
            />
          )}
        </div>
      </div>
    </div>
  );
}
