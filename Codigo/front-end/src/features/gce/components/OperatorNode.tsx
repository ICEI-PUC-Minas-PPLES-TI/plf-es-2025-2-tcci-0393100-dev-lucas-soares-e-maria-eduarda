import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import type { GCEFlowNode } from '../types/gce';

// AND: D-shape — flat left wall, true semicircle on the right.
//   The semicircle has center (50,30) and radius 30, so it touches
//   x=80 at y=30 exactly where the right handle sits.
const AND_PATH = 'M 1 1 L 50 1 A 29 29 0 0 1 50 59 L 1 59 Z';

// OR: shield/arrow — both sides are quadratic Bézier curves.
//   Right side meets at (80,30) — the right handle position.
//   Left side has a gentle concave inward curve (control at x=8)
//   so the gate is still clearly recognisable but the gap at the
//   left handle (x=0,y=30) is only ~2px visually.
const OR_PATH = 'M 1 1 Q 45 1 79 30 Q 45 59 1 59 Q 10 30 1 1 Z';

export const OperatorNode = memo(function OperatorNode({ id, data, selected }: NodeProps<GCEFlowNode>) {
  const { code, operatorType, hasError } = data;
  const gradId = `op-grad-${id}`;

  const borderColor = hasError
    ? 'var(--color-node-error)'
    : selected
      ? 'var(--color-node-selected)'
      : 'var(--color-edge)';

  const svgFilter = selected
    ? 'drop-shadow(0 0 8px rgba(240, 136, 62, 0.6)) drop-shadow(0 4px 6px rgba(0,0,0,0.4))'
    : 'drop-shadow(0 4px 6px rgba(0,0,0,0.4))';

  return (
    <div
      className="relative flex items-center justify-center transition-all duration-150"
      style={{ width: 80, height: 60 }}
    >
      <Handle id="left"   type="target" position={Position.Left}   className="w-3! h-3! bg-node-operator!" />
      <Handle id="top"    type="target" position={Position.Top}    className="w-3! h-3! bg-node-operator!" />
      <Handle id="bottom" type="target" position={Position.Bottom} className="w-3! h-3! bg-node-operator!" />
      <Handle id="right"  type="source" position={Position.Right}  className="w-3! h-3! bg-node-operator!" />

      <svg width="80" height="60" className="absolute inset-0" overflow="visible" style={{ filter: svgFilter, transition: 'filter 0.15s' }}>
        <defs>
          <linearGradient id={gradId} x1="0" y1="0" x2="80" y2="60" gradientUnits="userSpaceOnUse">
            <stop offset="0%" stopColor="#f0883e" />
            <stop offset="100%" stopColor="#92400e" />
          </linearGradient>
        </defs>
        <path
          d={operatorType === 'AND' ? AND_PATH : OR_PATH}
          fill={`url(#${gradId})`}
          stroke={borderColor}
          strokeWidth={2}
          strokeLinejoin="round"
        />
      </svg>

      <div
        className="relative z-10 text-center pointer-events-none"
        style={operatorType === 'OR' ? { transform: 'translateX(-10px)' } : undefined}
      >
        <span className="text-[9px] font-mono px-1 py-0.5 rounded bg-black/25 text-white/80 inline-block mb-0.5 tracking-wide">
          {code}
        </span>
        <span className="text-xs text-white font-bold block">{operatorType}</span>
      </div>
    </div>
  );
});
