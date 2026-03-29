import { memo } from 'react';
import { BaseEdge, getStraightPath, type EdgeProps } from '@xyflow/react';

export const NegationEdge = memo(function NegationEdge(props: EdgeProps) {
  const { sourceX, sourceY, targetX, targetY, selected } = props;

  const [edgePath] = getStraightPath({ sourceX, sourceY, targetX, targetY });

  const midX = (sourceX + targetX) / 2;
  const midY = (sourceY + targetY) / 2;

  return (
    <>
      <BaseEdge
        {...props}
        path={edgePath}
        style={{
          stroke: selected ? 'var(--color-node-selected)' : 'var(--color-edge-negation)',
          strokeWidth: selected ? 3 : 2,
          strokeDasharray: '6 4',
        }}
      />
      <circle
        cx={midX}
        cy={midY}
        r={5}
        fill="var(--color-edge-negation)"
        stroke="var(--color-surface)"
        strokeWidth={2}
      />
    </>
  );
});
