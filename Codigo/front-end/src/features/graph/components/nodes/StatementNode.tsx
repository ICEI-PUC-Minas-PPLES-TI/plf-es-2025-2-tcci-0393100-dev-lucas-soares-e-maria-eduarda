import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import type { GFCFlowNode } from '../../types/gfc';

export const StatementNode = memo(function StatementNode({ data, selected }: NodeProps<GFCFlowNode>) {
  const { code, label, startLine } = data;
  const borderColor = selected ? 'var(--color-node-selected)' : 'var(--color-edge)';

  return (
    <div
      className="group relative rounded-lg px-4 py-3 min-w-[160px] max-w-[220px] transition-all duration-150"
      style={{
        background: 'var(--color-gfc-statement-bg)',
        boxShadow: selected
          ? `inset 0 0 0 2px ${borderColor}, 0 0 16px rgba(88, 166, 255, 0.35), 0 4px 10px rgba(0,0,0,0.3)`
          : `inset 0 0 0 2px ${borderColor}, 0 4px 10px rgba(0,0,0,0.3)`,
      }}
    >
      <Handle id="top" type="target" position={Position.Top} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-statement)' }} />

      <span className="absolute -top-5 left-2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-white/80 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
        {code}
      </span>

      <div className="flex items-center gap-2">
        <span
          className="text-[9px] font-semibold uppercase tracking-wider px-1.5 py-0.5 rounded shrink-0"
          style={{ background: 'rgba(0,0,0,0.25)', color: 'rgba(255,255,255,0.8)' }}
        >
          comando
        </span>
        {startLine != null && (
          <span className="text-[9px] font-mono text-white/60 shrink-0">L{startLine}</span>
        )}
      </div>
      <div className="text-xs text-white font-mono mt-1 truncate" title={label}>
        {label}
      </div>

      <Handle id="bottom" type="source" position={Position.Bottom} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-statement)' }} />
    </div>
  );
});
