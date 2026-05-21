import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import type { GFCFlowNode, GFCNodeType } from '../../types/gfc';

interface BlockStyle {
  badge: string;
  fg: string;
  bg: string;
}

const STYLES: Partial<Record<GFCNodeType, BlockStyle>> = {
  CASE: { badge: 'case', fg: 'var(--color-gfc-case)', bg: 'var(--color-gfc-case-bg)' },
  CASE_BLOCK: { badge: 'corpo do case', fg: 'var(--color-gfc-case-block)', bg: 'var(--color-gfc-case-block-bg)' },
  TRY: { badge: 'try', fg: 'var(--color-gfc-try)', bg: 'var(--color-gfc-try-bg)' },
  CATCH: { badge: 'catch', fg: 'var(--color-gfc-catch)', bg: 'var(--color-gfc-catch-bg)' },
  FINALLY: { badge: 'finally', fg: 'var(--color-gfc-finally)', bg: 'var(--color-gfc-finally-bg)' },
};

const FALLBACK: BlockStyle = {
  badge: 'bloco',
  fg: 'var(--color-gfc-statement)',
  bg: 'var(--color-gfc-statement-bg)',
};

// Retângulo arredondado com cantos chanfrados (notch) — destaca blocos delimitadores.
// Compartilhado por CASE / TRY / CATCH / FINALLY.
export const BlockNode = memo(function BlockNode({ data, selected }: NodeProps<GFCFlowNode>) {
  const { code, label, nodeType, startLine } = data;
  const style = STYLES[nodeType] ?? FALLBACK;
  const borderColor = selected ? 'var(--color-node-selected)' : 'var(--color-edge)';

  return (
    <div
      className="group relative px-4 py-3 min-w-[160px] max-w-[220px] transition-all duration-150"
      style={{
        background: style.bg,
        clipPath: 'polygon(8px 0, calc(100% - 8px) 0, 100% 8px, 100% calc(100% - 8px), calc(100% - 8px) 100%, 8px 100%, 0 calc(100% - 8px), 0 8px)',
        boxShadow: selected
          ? `inset 0 0 0 2px ${borderColor}, 0 0 16px ${style.fg}55, 0 4px 10px rgba(0,0,0,0.3)`
          : `inset 0 0 0 2px ${borderColor}, 0 4px 10px rgba(0,0,0,0.3)`,
      }}
    >
      <Handle id="top" type="target" position={Position.Top} className="w-2.5! h-2.5!" style={{ background: style.fg }} />

      <span className="absolute -top-5 left-2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-white/80 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
        {code}
      </span>

      <div className="flex items-center gap-2">
        <span
          className="text-[9px] font-semibold uppercase tracking-wider px-1.5 py-0.5 rounded shrink-0"
          style={{ background: 'rgba(0,0,0,0.3)', color: 'rgba(255,255,255,0.85)' }}
        >
          {style.badge}
        </span>
        {startLine != null && (
          <span className="text-[9px] font-mono text-white/60 shrink-0">L{startLine}</span>
        )}
      </div>
      <div className="text-xs text-white font-mono mt-1 truncate" title={label}>
        {label}
      </div>

      <Handle id="bottom" type="source" position={Position.Bottom} className="w-2.5! h-2.5!" style={{ background: style.fg }} />
      <Handle id="left" type="source" position={Position.Left} className="w-2.5! h-2.5!" style={{ background: style.fg }} />
      <Handle id="right" type="source" position={Position.Right} className="w-2.5! h-2.5!" style={{ background: style.fg }} />
    </div>
  );
});
