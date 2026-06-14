import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import { Play } from 'lucide-react';
import type { GFCFlowNode } from '../../types/gfc';

export const StartNode = memo(function StartNode({ data, selected }: NodeProps<GFCFlowNode>) {
  const { code, label } = data;
  const borderColor = selected ? 'var(--color-node-selected)' : 'var(--color-edge)';

  return (
    <div
      className="group relative flex items-center gap-2 px-4 py-2 transition-all duration-150"
      style={{
        background: 'var(--color-gfc-start-bg)',
        borderRadius: 999,
        boxShadow: selected
          ? `inset 0 0 0 2px ${borderColor}, 0 0 14px rgba(63, 185, 80, 0.4), 0 4px 10px rgba(0,0,0,0.3)`
          : `inset 0 0 0 2px ${borderColor}, 0 4px 10px rgba(0,0,0,0.3)`,
      }}
    >
      <span className="absolute -top-5 left-2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-white/80 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
        {code}
      </span>

      <Play className="w-3.5 h-3.5 text-white" fill="currentColor" />
      <span className="text-xs text-white font-semibold uppercase tracking-wider">
        {label || 'Início'}
      </span>

      <Handle id="bottom" type="source" position={Position.Bottom} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-start)' }} />
    </div>
  );
});
