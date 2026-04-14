import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import type { GCEFlowNode } from '../types/gce';

export const OperatorNode = memo(function OperatorNode({ id, data, selected }: NodeProps<GCEFlowNode>) {
  const { code, operatorType, hasError } = data;
  const gradId = `op-grad-${id}`;

  const borderColor = hasError
    ? 'var(--color-node-error)'
    : selected
      ? 'var(--color-node-selected)'
      : 'var(--color-edge)';

  return (
    <div
      className="relative flex items-center justify-center transition-all duration-150"
      style={{
        width: 80,
        height: 60,
        boxShadow: selected
          ? '0 0 20px rgba(240, 136, 62, 0.4), 0 4px 12px rgba(0,0,0,0.4)'
          : '0 4px 12px rgba(0,0,0,0.4)',
      }}
    >
      <Handle id="left" type="target" position={Position.Left} className="w-3! h-3! bg-node-operator!" />
      <Handle id="top" type="source" position={Position.Top} className="w-3! h-3! bg-node-operator!" />
      <Handle id="bottom" type="source" position={Position.Bottom} className="w-3! h-3! bg-node-operator!" />

      <svg width="80" height="60" className="absolute inset-0">
        <defs>
          <linearGradient id={gradId} x1="0" y1="0" x2="80" y2="60" gradientUnits="userSpaceOnUse">
            <stop offset="0%" stopColor="#f0883e" />
            <stop offset="100%" stopColor="#92400e" />
          </linearGradient>
        </defs>
        {operatorType === 'AND' && (
          <path
            d="M 0 5 L 45 5 Q 75 5 75 30 Q 75 55 45 55 L 0 55 Z"
            fill={`url(#${gradId})`}
            stroke={borderColor}
            strokeWidth={2}
          />
        )}
        {operatorType === 'OR' && (
          <path
            d="M 10 5 L 45 5 Q 75 30 45 55 L 10 55 Q 30 30 10 5 Z"
            fill={`url(#${gradId})`}
            stroke={borderColor}
            strokeWidth={2}
          />
        )}
      </svg>

      <div className="relative z-10 text-center">
        <span className="text-[9px] font-mono px-1 py-0.5 rounded bg-black/25 text-white/80 inline-block mb-0.5 tracking-wide">
          {code}
        </span>
        <span className="text-xs text-white font-bold block">{operatorType}</span>
      </div>

      <Handle id="right" type="source" position={Position.Right} className="w-3! h-3! bg-node-operator!" />
    </div>
  );
});
