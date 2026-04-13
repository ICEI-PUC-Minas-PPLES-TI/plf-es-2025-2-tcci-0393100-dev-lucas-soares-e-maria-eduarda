import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import type { GCEFlowNode } from '../types/gce';

export const OperatorNode = memo(function OperatorNode({ data, selected }: NodeProps<GCEFlowNode>) {
  const { code, operatorType, hasError } = data;

  const borderColor = hasError
    ? 'var(--color-node-error)'
    : selected
      ? 'var(--color-node-selected)'
      : 'var(--color-edge)';

  return (
    <div
      className="relative flex items-center justify-center transition-shadow"
      style={{
        width: 80,
        height: 60,
        boxShadow: selected ? '0 0 12px rgba(240, 136, 62, 0.3)' : 'none',
      }}
    >
      <Handle id="left" type="target" position={Position.Left} className="w-2! h-2! bg-node-operator!" />
      <Handle id="top" type="source" position={Position.Top} className="w-2! h-2! bg-node-operator!" />
      <Handle id="bottom" type="source" position={Position.Bottom} className="w-2! h-2! bg-node-operator!" />

      <svg width="80" height="60" className="absolute inset-0">
        {operatorType === 'AND' && (
          <path
            d="M 0 5 L 45 5 Q 75 5 75 30 Q 75 55 45 55 L 0 55 Z"
            fill="var(--color-node-operator-bg)"
            stroke={borderColor}
            strokeWidth={2}
          />
        )}
        {operatorType === 'OR' && (
          <path
            d="M 10 5 L 45 5 Q 75 30 45 55 L 10 55 Q 30 30 10 5 Z"
            fill="var(--color-node-operator-bg)"
            stroke={borderColor}
            strokeWidth={2}
          />
        )}
      </svg>

      <div className="relative z-10 text-center">
        <span className="text-[10px] text-white/60 block">{code}</span>
        <span className="text-xs text-white font-bold">{operatorType}</span>
      </div>

      <Handle id="right" type="source" position={Position.Right} className="w-2! h-2! bg-node-operator!" />
    </div>
  );
});
