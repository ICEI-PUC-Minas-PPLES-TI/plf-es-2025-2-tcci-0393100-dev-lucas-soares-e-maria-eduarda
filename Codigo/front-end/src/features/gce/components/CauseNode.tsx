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
      className="rounded-lg px-4 py-3 min-w-35 transition-shadow"
      style={{
        background: 'var(--color-node-cause-bg)',
        border: `2px solid ${borderColor}`,
        boxShadow: selected ? '0 0 12px rgba(63, 185, 80, 0.3)' : 'none',
      }}
    >
      <Handle type="target" position={Position.Left} className="w-2! h-2! bg-node-cause!" />

      <div className="flex items-center gap-2">
        <Circle className="w-4 h-4 shrink-0" style={{ color: 'var(--color-node-cause)' }} />
        <div className="min-w-0">
          <span className="text-[10px] text-white/60 block">{code}</span>
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

      <Handle type="source" position={Position.Right} className="w-2! h-2! bg-node-cause!" />
    </div>
  );
});
