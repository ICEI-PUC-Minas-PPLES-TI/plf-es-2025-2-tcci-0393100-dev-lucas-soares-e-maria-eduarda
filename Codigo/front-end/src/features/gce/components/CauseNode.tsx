import { memo } from 'react';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import { Circle } from 'lucide-react';
import type { GCEFlowNode } from '../types/gce';
import { useInlineEdit } from '../hooks/useInlineEdit';

export const CauseNode = memo(function CauseNode({ id, data, selected }: NodeProps<GCEFlowNode>) {
  const { code, label, hasError } = data;
  const { editing, draft, setDraft, inputRef, handleDoubleClick, commit, handleKeyDown } = useInlineEdit(id, label);

  const borderColor = hasError
    ? 'var(--color-node-error)'
    : selected
      ? 'var(--color-node-selected)'
      : 'var(--color-edge)';

  return (
    <div
      className="group relative rounded-lg px-4 py-3 min-w-35 transition-all duration-150"
      style={{
        background: 'linear-gradient(145deg, #2ea043 0%, #196127 100%)',
        boxShadow: selected
          ? `inset 0 0 0 2px ${borderColor}, 0 0 20px rgba(63, 185, 80, 0.4), 0 4px 12px rgba(0,0,0,0.4)`
          : `inset 0 0 0 2px ${borderColor}, 0 4px 12px rgba(0,0,0,0.4)`,
      }}
    >
      <Handle id="left" type="target" position={Position.Left} className="w-3! h-3! bg-node-cause!" />
      <Handle id="top" type="source" position={Position.Top} className="w-3! h-3! bg-node-cause!" />
      <Handle id="bottom" type="source" position={Position.Bottom} className="w-3! h-3! bg-node-cause!" />

      <span className="absolute -top-5 left-2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-white/80 tracking-wide opacity-0 group-hover:opacity-100 transition-opacity duration-150 pointer-events-none">
        {code}
      </span>

      <div className="flex items-center gap-2">
        <Circle className="w-4 h-4 shrink-0" style={{ color: 'var(--color-node-cause)' }} />
        <div className="min-w-0">
          {editing ? (
            <input
              ref={inputRef}
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              onBlur={commit}
              onKeyDown={handleKeyDown}
              className="text-xs text-white bg-transparent border-b border-white/40 outline-none w-full"
            />
          ) : (
            <span
              className="text-xs text-white truncate block cursor-text"
              onDoubleClick={handleDoubleClick}
              title="Clique duplo para editar"
            >
              {label}
            </span>
          )}
        </div>
      </div>

      <Handle id="right" type="source" position={Position.Right} className="w-3! h-3! bg-node-cause!" />
    </div>
  );
});
