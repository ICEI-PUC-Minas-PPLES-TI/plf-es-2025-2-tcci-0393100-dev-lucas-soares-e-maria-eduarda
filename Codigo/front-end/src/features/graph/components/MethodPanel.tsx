import { useState, useMemo } from 'react';
import { Search, ChevronLeft, ChevronRight, CheckCircle2, Circle, ListTree, Loader2 } from 'lucide-react';
import type { GFCSourceMethodDTO, GFCSummaryDTO } from '../types/gfc';

interface MethodPanelProps {
  isCollapsed: boolean;
  onToggleCollapse: () => void;
  methods: GFCSourceMethodDTO[];
  gfcs: GFCSummaryDTO[];
  currentMethodSignature: string;
  generatingSignature: string | null;
  onSelectMethod: (method: GFCSourceMethodDTO) => void;
  fileName: string | null;
}

export function MethodPanel({
  isCollapsed,
  onToggleCollapse,
  methods,
  gfcs,
  currentMethodSignature,
  generatingSignature,
  onSelectMethod,
  fileName,
}: MethodPanelProps) {
  const [searchQuery, setSearchQuery] = useState('');

  const gfcBySignature = useMemo(() => {
    const map = new Map<string, GFCSummaryDTO>();
    gfcs.forEach((g) => map.set(g.methodSignature, g));
    return map;
  }, [gfcs]);

  const filtered = useMemo(
    () =>
      methods.filter((m) =>
        m.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        m.signature.toLowerCase().includes(searchQuery.toLowerCase()),
      ),
    [methods, searchQuery],
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
        title="Expandir lista de métodos"
        className="w-10 bg-surface-card border-r border-edge flex flex-col items-center pt-3 gap-2 shrink-0 cursor-pointer hover:bg-surface-hover transition-colors"
      >
        <ChevronRight className="w-4 h-4 text-gray-400" />
        <ListTree className="w-4 h-4 text-gray-500" aria-hidden />
        <span className="[writing-mode:vertical-rl] text-[10px] tracking-wider uppercase text-gray-500 mt-1">
          Métodos
        </span>
      </div>
    );
  }

  return (
    <div className="w-72 bg-surface-card border-r border-edge flex flex-col shrink-0">
      <div className="p-3 border-b border-edge">
        <div className="flex items-center justify-between mb-2">
          <div className="min-w-0">
            <h2 className="text-sm text-gray-200 font-medium">Métodos</h2>
            {fileName && (
              <p className="text-xs text-gray-500 truncate font-mono" title={fileName}>
                {fileName}
              </p>
            )}
          </div>
          <button
            onClick={onToggleCollapse}
            className="p-1.5 text-gray-400 hover:text-gray-200 hover:bg-surface-hover rounded transition-colors shrink-0"
            title="Recolher painel"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
        </div>

        <div className="relative">
          <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-500" />
          <input
            type="text"
            placeholder="Buscar método..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-8 pr-2 py-1.5 text-sm bg-surface border border-edge rounded text-gray-200 placeholder:text-gray-500 focus:border-primary focus:outline-none"
          />
        </div>
      </div>

      <div className="flex-1 overflow-y-auto p-2">
        {filtered.length === 0 ? (
          <div className="text-center py-8 text-sm text-gray-500">
            {methods.length === 0 ? 'Nenhum método encontrado no arquivo.' : 'Nenhum método corresponde à busca.'}
          </div>
        ) : (
          filtered.map((method) => {
            const existing = gfcBySignature.get(method.signature);
            const isCurrent = method.signature === currentMethodSignature;
            const isGenerating = generatingSignature === method.signature;

            return (
              <button
                key={method.signature}
                onClick={() => onSelectMethod(method)}
                disabled={isGenerating}
                className={`w-full text-left p-2.5 rounded-lg mb-1 transition-colors disabled:opacity-60 disabled:cursor-wait ${
                  isCurrent
                    ? 'bg-primary/15 border border-primary/50'
                    : 'hover:bg-surface-hover border border-transparent'
                }`}
              >
                <div className="flex items-center gap-2">
                  {isGenerating ? (
                    <Loader2 className="w-3.5 h-3.5 text-primary-light animate-spin shrink-0" />
                  ) : existing ? (
                    <CheckCircle2 className="w-3.5 h-3.5 text-green-400 shrink-0" />
                  ) : (
                    <Circle className="w-3.5 h-3.5 text-gray-600 shrink-0" />
                  )}
                  <span
                    className={`text-sm font-mono truncate ${
                      isCurrent ? 'text-primary-light' : 'text-gray-200'
                    }`}
                    title={method.name}
                  >
                    {method.name}()
                  </span>
                </div>
                <div className="flex items-center gap-2 mt-0.5 ml-5.5 pl-1">
                  <span className="text-xs text-gray-500 font-mono">
                    L{method.startLine}–{method.endLine}
                  </span>
                  {!existing && !isGenerating && (
                    <span className="text-xs text-gray-600">· clicar para gerar</span>
                  )}
                </div>
              </button>
            );
          })
        )}
      </div>
    </div>
  );
}
