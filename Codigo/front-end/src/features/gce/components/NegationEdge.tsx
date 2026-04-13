import { memo } from 'react';
import { BaseEdge, type EdgeProps } from '@xyflow/react';

function buildNegationPath(
  sourceX: number,
  sourceY: number,
  targetX: number,
  targetY: number,
  waveWidth = 40,
  amplitude = 10,
): string {
  const dx = targetX - sourceX;
  const dy = targetY - sourceY;
  const length = Math.sqrt(dx * dx + dy * dy);
  if (length === 0) return `M ${sourceX} ${sourceY}`;

  const ux = dx / length; // vetor unitário ao longo da aresta
  const uy = dy / length;
  const px = -uy; // vetor perpendicular
  const py = ux;

  const midX = (sourceX + targetX) / 2;
  const midY = (sourceY + targetY) / 2;

  // Início e fim da seção ondulada, centrada no meio da aresta
  const waveStartX = midX - (waveWidth / 2) * ux;
  const waveStartY = midY - (waveWidth / 2) * uy;
  const waveEndX = midX + (waveWidth / 2) * ux;
  const waveEndY = midY + (waveWidth / 2) * uy;

  // Primeiro meio-período: waveStart → mid (curva para cima)
  const cp1x = waveStartX + (midX - waveStartX) / 3 + amplitude * px;
  const cp1y = waveStartY + (midY - waveStartY) / 3 + amplitude * py;
  const cp2x = waveStartX + (2 * (midX - waveStartX)) / 3 + amplitude * px;
  const cp2y = waveStartY + (2 * (midY - waveStartY)) / 3 + amplitude * py;

  // Segundo meio-período: mid → waveEnd (curva para baixo)
  const cp3x = midX + (waveEndX - midX) / 3 - amplitude * px;
  const cp3y = midY + (waveEndY - midY) / 3 - amplitude * py;
  const cp4x = midX + (2 * (waveEndX - midX)) / 3 - amplitude * px;
  const cp4y = midY + (2 * (waveEndY - midY)) / 3 - amplitude * py;

  return [
    `M ${sourceX} ${sourceY}`,
    `L ${waveStartX} ${waveStartY}`,
    `C ${cp1x},${cp1y} ${cp2x},${cp2y} ${midX},${midY}`,
    `C ${cp3x},${cp3y} ${cp4x},${cp4y} ${waveEndX},${waveEndY}`,
    `L ${targetX} ${targetY}`,
  ].join(' ');
}

export const NegationEdge = memo(function NegationEdge(props: EdgeProps) {
  const { sourceX, sourceY, targetX, targetY, selected } = props;

  const path = buildNegationPath(sourceX, sourceY, targetX, targetY);

  return (
    <BaseEdge
      {...props}
      path={path}
      style={{
        stroke: selected ? 'var(--color-node-selected)' : 'var(--color-edge-hover)',
        strokeWidth: selected ? 3 : 2,
      }}
    />
  );
});
