import { useState, useRef, useCallback, useEffect } from 'react';
import { useReactFlow } from '@xyflow/react';

export function useInlineEdit(nodeId: string, currentLabel: string) {
  const { setNodes } = useReactFlow();
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(currentLabel);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (editing) {
      setDraft(currentLabel);
      inputRef.current?.focus();
      inputRef.current?.select();
    }
  }, [editing, currentLabel]);

  const commit = useCallback(() => {
    const trimmed = draft.trim();
    if (trimmed) {
      setNodes((nds) =>
        nds.map((n) =>
          n.id === nodeId ? { ...n, data: { ...n.data, label: trimmed } } : n,
        ),
      );
    }
    setEditing(false);
  }, [draft, nodeId, setNodes]);

  const handleDoubleClick = useCallback((e: React.MouseEvent) => {
    e.stopPropagation();
    setEditing(true);
  }, []);

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (e.key === 'Enter') commit();
      if (e.key === 'Escape') setEditing(false);
    },
    [commit],
  );

  return { editing, draft, setDraft, inputRef, handleDoubleClick, commit, handleKeyDown };
}
