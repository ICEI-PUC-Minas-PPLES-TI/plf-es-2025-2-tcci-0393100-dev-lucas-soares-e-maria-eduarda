import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import { CornerDownRight, RotateCw, AlertTriangle, type LucideIcon } from 'lucide-react';
import type { GFCFlowNode, GFCNodeType } from '../../types/gfc';

interface TerminatorStyle {
  badge: string;
  fg: string;
  bg: string;
  icon: LucideIcon;
  glow: string;
}

const STYLES: Partial<Record<GFCNodeType, TerminatorStyle>> = {
  BREAK: {
    badge: 'break',
    fg: 'var(--color-gfc-break)',
    bg: 'var(--color-gfc-break-bg)',
    icon: CornerDownRight,
    glow: 'rgba(251, 146, 60, 0.4)',
  },
  CONTINUE: {
    badge: 'continue',
    fg: 'var(--color-gfc-continue)',
    bg: 'var(--color-gfc-continue-bg)',
    icon: RotateCw,
    glow: 'rgba(192, 132, 252, 0.4)',
  },
  THROW: {
    badge: 'throw',
    fg: 'var(--color-gfc-throw)',
    bg: 'var(--color-gfc-throw-bg)',
    icon: AlertTriangle,
    glow: 'rgba(248, 113, 113, 0.4)',
  },
};

const FALLBACK: TerminatorStyle = {
  badge: 'fluxo',
  fg: 'var(--color-gfc-return)',
  bg: 'var(--color-gfc-return-bg)',
  icon: CornerDownRight,
  glow: 'rgba(248, 81, 73, 0.4)',
};

// Pill horizontal estilo "saída de fluxo" — usado por BREAK, CONTINUE e THROW.
export const TerminatorNode = memo(function TerminatorNode({ data, selected }: NodeProps<GFCFlowNode>) {
  const { code, label, nodeType, startLine } = data;
  const style = STYLES[nodeType] ?? FALLBACK;
  const Icon = style.icon;
  const borderColor = selected ? 'var(--color-node-selected)' : 'var(--color-edge)';

  return (
    <div
      className="group relative flex items-center gap-2 px-3 py-2 transition-all duration-150 min-w-[120px] max-w-[220px]"
      style={{
        background: style.bg,
        borderRadius: 999,
        boxShadow: selected
          ? `inset 0 0 0 2px ${borderColor}, 0 0 14px ${style.glow}, 0 4px 10px rgba(0,0,0,0.3)`
          : `inset 0 0 0 2px ${borderColor}, 0 4px 10px rgba(0,0,0,0.3)`,
      }}
    >
      <Handle id="top" type="target" position={Position.Top} className="w-2.5! h-2.5!" style={{ background: style.fg }} />

      <span className="absolute -top-5 left-2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-white/80 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
        {code}
      </span>

      <Icon className="w-3.5 h-3.5 text-white shrink-0" />
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5">
          <span className="text-[9px] font-semibold uppercase tracking-wider text-white/85">
            {style.badge}
          </span>
          {startLine != null && (
            <span className="text-[9px] font-mono text-white/65">L{startLine}</span>
          )}
        </div>
        {label && (
          <div className="text-[11px] text-white font-mono truncate" title={label}>
            {label}
          </div>
        )}
      </div>

      <Handle id="bottom" type="source" position={Position.Bottom} className="w-2.5! h-2.5!" style={{ background: style.fg }} />
      <Handle id="left" type="source" position={Position.Left} className="w-2.5! h-2.5!" style={{ background: style.fg }} />
      <Handle id="right" type="source" position={Position.Right} className="w-2.5! h-2.5!" style={{ background: style.fg }} />
    </div>
  );
});
