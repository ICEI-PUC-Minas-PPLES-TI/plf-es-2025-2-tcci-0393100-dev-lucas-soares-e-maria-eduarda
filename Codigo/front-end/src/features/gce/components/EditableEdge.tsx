import { memo, useState, useCallback } from 'react';
import { BaseEdge, EdgeLabelRenderer, useReactFlow, type EdgeProps } from '@xyflow/react';
import type { GCEEdgeData } from '../types/gce';

export const EditableEdge = memo(function EditableEdge(props: EdgeProps) {
  const { id, sourceX, sourceY, targetX, targetY, selected, data } = props;
  const { setEdges, screenToFlowPosition } = useReactFlow();
  const [hovered, setHovered] = useState(false);

  const bend = (data as GCEEdgeData | undefined)?.bend;
  const midX = (sourceX + targetX) / 2;
  const midY = (sourceY + targetY) / 2;
  const bx = bend?.x ?? midX;
  const by = bend?.y ?? midY;

  // Ponto de controle da bezier quadrática que passa por (bx, by) em t=0.5
  const cpX = 2 * bx - 0.5 * (sourceX + targetX);
  const cpY = 2 * by - 0.5 * (sourceY + targetY);
  const edgePath = `M ${sourceX},${sourceY} Q ${cpX},${cpY} ${targetX},${targetY}`;

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    e.stopPropagation();
    const onMove = (me: MouseEvent) => {
      const pos = screenToFlowPosition({ x: me.clientX, y: me.clientY });
      setEdges((eds) =>
        eds.map((ed) => ed.id === id ? { ...ed, data: { ...ed.data, bend: pos } } : ed),
      );
    };
    const onUp = () => {
      document.removeEventListener('mousemove', onMove);
      document.removeEventListener('mouseup', onUp);
    };
    document.addEventListener('mousemove', onMove);
    document.addEventListener('mouseup', onUp);
  }, [id, setEdges, screenToFlowPosition]);

  const showHandle = selected || hovered;

  return (
    <>
      <BaseEdge
        {...props}
        path={edgePath}
        style={{
          stroke: selected ? 'var(--color-node-selected)' : 'var(--color-edge-hover)',
          strokeWidth: selected ? 3 : 2,
        }}
      />
      <EdgeLabelRenderer>
        <div
          style={{
            position: 'absolute',
            transform: `translate(-50%, -50%) translate(${bx}px,${by}px)`,
            pointerEvents: 'all',
            cursor: 'grab',
            width: 20,
            height: 20,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
          className="nodrag nopan"
          onMouseDown={handleMouseDown}
          onMouseEnter={() => setHovered(true)}
          onMouseLeave={() => setHovered(false)}
        >
          <div
            style={{
              width: 8,
              height: 8,
              borderRadius: '50%',
              background: 'var(--color-edge-hover)',
              border: '2px solid var(--color-surface)',
              opacity: showHandle ? 1 : 0,
              transition: 'opacity 0.15s',
            }}
          />
        </div>
      </EdgeLabelRenderer>
    </>
  );
});
