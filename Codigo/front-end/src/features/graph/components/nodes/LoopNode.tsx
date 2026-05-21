import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import type { GFCFlowNode } from '../../types/gfc';

// Hexágono horizontal, sugerindo iteração (entradas/saídas múltiplas).
export const LoopNode = memo(function LoopNode({ data, selected }: NodeProps<GFCFlowNode>) {
  const { code, label, startLine } = data;
  const borderColor = selected ? 'var(--color-node-selected)' : 'var(--color-edge)';
  const width = 200;
  const height = 90;

  const points = `30,2 ${width - 30},2 ${width - 2},${height / 2} ${width - 30},${height - 2} 30,${height - 2} 2,${height / 2}`;

  return (
    <div
      className="group relative flex items-center justify-center"
      style={{ width, height }}
    >
      <Handle id="top" type="target" position={Position.Top} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-loop)' }} />
      <Handle id="loopback" type="target" position={Position.Left} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-edge-loop)', top: '30%' }} />
      <Handle id="left" type="source" position={Position.Left} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-edge-loop)', top: '70%' }} />
      <Handle id="right" type="source" position={Position.Right} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-edge-exit)' }} />
      <Handle id="bottom" type="source" position={Position.Bottom} className="w-2.5! h-2.5!" style={{ background: 'var(--color-gfc-edge-body)' }} />

      <svg
        width={width}
        height={height}
        className="absolute inset-0"
        style={{
          filter: selected
            ? 'drop-shadow(0 0 10px rgba(168, 85, 247, 0.5)) drop-shadow(0 4px 8px rgba(0,0,0,0.3))'
            : 'drop-shadow(0 4px 8px rgba(0,0,0,0.3))',
          transition: 'filter 0.15s',
        }}
      >
        <polygon
          points={points}
          fill="var(--color-gfc-loop-bg)"
          stroke={borderColor}
          strokeWidth={2}
        />
      </svg>

      <span className="absolute -top-5 left-1/2 -translate-x-1/2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-white/80 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none whitespace-nowrap">
        {code}
      </span>

      <div className="relative z-10 text-center px-8 pointer-events-none">
        <div className="flex items-center justify-center gap-1.5 mb-0.5">
          <span
            className="text-[9px] font-semibold uppercase tracking-wider px-1.5 py-0.5 rounded"
            style={{ background: 'rgba(0,0,0,0.25)', color: 'rgba(255,255,255,0.9)' }}
          >
            loop
          </span>
          {startLine != null && (
            <span className="text-[9px] font-mono text-white/70">L{startLine}</span>
          )}
        </div>
        <div className="text-[11px] text-white font-mono leading-tight line-clamp-2" title={label}>
          {label}
        </div>
      </div>
    </div>
  );
});
