import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import { Square } from 'lucide-react';
import type { GCEFlowNode } from '../types/gce';

export const EffectNode = memo(function EffectNode({ data, selected }: NodeProps<GCEFlowNode>) {
  const { code, label, hasError } = data;

  const borderColor = hasError
    ? 'var(--color-node-error)'
    : selected
      ? 'var(--color-node-selected)'
      : 'var(--color-edge)';

  return (
    <div
      className="rounded-lg px-4 py-3 min-w-35 transition-shadow"
      style={{
        background: 'var(--color-node-effect-bg)',
        border: `2px solid ${borderColor}`,
        boxShadow: selected ? '0 0 12px rgba(88, 166, 255, 0.3)' : 'none',
      }}
    >
      <Handle type="target" position={Position.Left} className="w-2! h-2! bg-node-effect!" />

      <div className="flex items-center gap-2">
        <Square className="w-4 h-4 shrink-0" style={{ color: 'var(--color-node-effect)' }} />
        <div className="min-w-0">
          <span className="text-[10px] text-white/60 block">{code}</span>
          <span className="text-xs text-white truncate block">{label}</span>
        </div>
      </div>

      <Handle type="source" position={Position.Right} className="w-2! h-2! bg-node-effect!" />
    </div>
  );
});
