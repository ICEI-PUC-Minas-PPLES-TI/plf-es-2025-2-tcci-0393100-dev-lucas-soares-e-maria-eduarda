import { ChevronLeft, ChevronRight, Code2, FileCode, Hash, FileText, Info, Tag } from 'lucide-react';
import type { GFCNodeType } from '../types/gfc';

interface NodeInfo {
  code: string;
  label: string;
  nodeType: GFCNodeType;
  startLine: number | null;
  endLine: number | null;
}

interface NodeInfoPanelProps {
  node: NodeInfo | null;
  isCollapsed: boolean;
  onToggleCollapse: () => void;
  onViewInSource?: () => void;
  canViewInSource?: boolean;
}

const TYPE_LABEL: Record<GFCNodeType, string> = {
  START: 'Início',
  END: 'Fim',
  STATEMENT: 'Comando',
  DECISION: 'Decisão',
  LOOP: 'Loop',
  RETURN: 'Retorno',
  BREAK: 'Break',
  CONTINUE: 'Continue',
  THROW: 'Throw',
  SWITCH: 'Switch',
  CASE: 'Case',
  CASE_BLOCK: 'Corpo do Case',
  TRY: 'Try',
  CATCH: 'Catch',
  FINALLY: 'Finally',
  TERNARY: 'Ternário',
};

const TYPE_BADGE: Record<GFCNodeType, string> = {
  START: 'bg-green-500/15 text-green-400 border-green-500/40',
  END: 'bg-slate-500/15 text-slate-400 border-slate-500/40',
  STATEMENT: 'bg-blue-500/15 text-blue-400 border-blue-500/40',
  DECISION: 'bg-yellow-500/15 text-yellow-400 border-yellow-500/40',
  LOOP: 'bg-purple-500/15 text-purple-400 border-purple-500/40',
  RETURN: 'bg-red-500/15 text-red-400 border-red-500/40',
  BREAK: 'bg-orange-500/15 text-orange-400 border-orange-500/40',
  CONTINUE: 'bg-purple-500/15 text-purple-400 border-purple-500/40',
  THROW: 'bg-red-500/15 text-red-400 border-red-500/40',
  SWITCH: 'bg-orange-500/15 text-orange-400 border-orange-500/40',
  CASE: 'bg-orange-500/15 text-orange-300 border-orange-500/30',
  CASE_BLOCK: 'bg-orange-500/15 text-orange-400 border-orange-500/40',
  TRY: 'bg-teal-500/15 text-teal-400 border-teal-500/40',
  CATCH: 'bg-amber-500/15 text-amber-400 border-amber-500/40',
  FINALLY: 'bg-slate-500/15 text-slate-400 border-slate-500/40',
  TERNARY: 'bg-fuchsia-500/15 text-fuchsia-400 border-fuchsia-500/40',
};

export function NodeInfoPanel({
  node,
  isCollapsed,
  onToggleCollapse,
  onViewInSource,
  canViewInSource = false,
}: NodeInfoPanelProps) {
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
        title="Expandir informações do nó"
        className="w-10 bg-surface-card border-l border-edge flex flex-col items-center pt-3 gap-2 shrink-0 cursor-pointer hover:bg-surface-hover transition-colors"
      >
        <ChevronLeft className="w-4 h-4 text-gray-400" />
        <Info className="w-4 h-4 text-gray-500" aria-hidden />
        <span className="[writing-mode:vertical-rl] rotate-180 text-[10px] tracking-wider uppercase text-gray-500 mt-1">
          Info do nó
        </span>
      </div>
    );
  }

  return (
    <div className="w-72 bg-surface-card border-l border-edge flex flex-col shrink-0">
      <div className="p-3 border-b border-edge flex items-center justify-between">
        <h2 className="text-sm text-gray-200 font-medium">Informações do Nó</h2>
        <button
          onClick={onToggleCollapse}
          className="p-1.5 text-gray-400 hover:text-gray-200 hover:bg-surface-hover rounded transition-colors"
          title="Recolher painel"
        >
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>

      {!node ? (
        <div className="flex-1 flex items-center justify-center p-4">
          <p className="text-sm text-gray-500 text-center">
            Selecione um nó no grafo para ver suas informações.
          </p>
        </div>
      ) : (
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          <Field label="Tipo" icon={Tag}>
            <span className={`inline-block px-2 py-0.5 rounded text-xs border ${TYPE_BADGE[node.nodeType]}`}>
              {TYPE_LABEL[node.nodeType]}
            </span>
          </Field>

          <Field label="Código" icon={Hash}>
            <div className="font-mono text-sm text-gray-200 bg-surface border border-edge rounded px-3 py-2">
              {node.code}
            </div>
          </Field>

          <Field label="Rótulo" icon={Code2}>
            <div className="font-mono text-sm text-primary-light bg-surface border border-edge rounded px-3 py-2 break-all">
              {node.label || '—'}
            </div>
          </Field>

          {(node.startLine != null || node.endLine != null) && (
            <Field label="Linhas no código" icon={FileText}>
              <div className="font-mono text-sm text-gray-200 bg-surface border border-edge rounded px-3 py-2">
                {node.startLine === node.endLine || node.endLine == null
                  ? `Linha ${node.startLine ?? '?'}`
                  : `Linha ${node.startLine ?? '?'} – ${node.endLine}`}
              </div>
            </Field>
          )}

          {canViewInSource && onViewInSource && node.startLine != null && (
            <button
              onClick={onViewInSource}
              className="w-full flex items-center justify-center gap-2 px-3 py-2 text-sm text-primary-light bg-primary/10 hover:bg-primary/20 border border-primary/30 rounded transition-colors"
            >
              <FileCode className="w-4 h-4" />
              Ver no arquivo
            </button>
          )}
        </div>
      )}
    </div>
  );
}

function Field({
  label,
  icon: Icon,
  children,
}: {
  label: string;
  icon: typeof Tag;
  children: React.ReactNode;
}) {
  return (
    <div>
      <div className="flex items-center gap-1.5 mb-1.5">
        <Icon className="w-3.5 h-3.5 text-gray-500" />
        <span className="text-xs text-gray-500">{label}</span>
      </div>
      {children}
    </div>
  );
}
