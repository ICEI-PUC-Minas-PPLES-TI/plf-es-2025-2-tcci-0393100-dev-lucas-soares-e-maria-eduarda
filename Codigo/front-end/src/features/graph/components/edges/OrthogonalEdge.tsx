import { BaseEdge, EdgeLabelRenderer, Position, type EdgeProps } from '@xyflow/react';
import type { GFCFlowEdgeData } from '../../types/gfc';

const HANDLE_CLEARANCE = 24;
// Acima desse offset entre o startPoint/endPoint do ELK e o handle atual do
// React Flow, consideramos que o nó foi arrastado e os bend points ficaram
// desalinhados — aí roteamos client-side em vez de usar os bends do layout.
const STALE_THRESHOLD = 16;

/**
 * Aresta ortogonal que segue os bend points calculados pelo ELK enquanto os
 * nós estão nas posições do layout. Quando o usuário arrasta um endpoint os
 * bends ficam obsoletos; nesse caso fazemos um roteamento Z-shape simples
 * baseado nos handles atuais, mantendo a aresta reta sempre que possível.
 */
export function OrthogonalEdge(props: EdgeProps) {
  const {
    id, sourceX, sourceY, targetX, targetY,
    sourcePosition, targetPosition,
    style, markerEnd, label, labelStyle,
    labelBgStyle, labelBgPadding, labelBgBorderRadius,
  } = props;
  const data = props.data as GFCFlowEdgeData | undefined;
  const bends = data?.bendPoints ?? [];

  const elkStart = bends[0];
  const elkEnd = bends[bends.length - 1];
  const stale =
    bends.length >= 2 &&
    ((elkStart != null &&
      (Math.abs(elkStart.x - sourceX) > STALE_THRESHOLD ||
        Math.abs(elkStart.y - sourceY) > STALE_THRESHOLD)) ||
      (elkEnd != null &&
        (Math.abs(elkEnd.x - targetX) > STALE_THRESHOLD ||
          Math.abs(elkEnd.y - targetY) > STALE_THRESHOLD)));

  const points: { x: number; y: number }[] = [{ x: sourceX, y: sourceY }];

  if (bends.length > 2 && !stale) {
    // Usa os bends do ELK (já são ortogonais e evitam cruzamentos).
    bends.slice(1, -1).forEach((p) => points.push(p));
  } else {
    addOrthoBends(points, sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition);
  }

  points.push({ x: targetX, y: targetY });

  let path = `M ${points[0].x} ${points[0].y}`;
  for (let i = 1; i < points.length; i++) {
    path += ` L ${points[i].x} ${points[i].y}`;
  }

  const labelPoint = points[Math.floor(points.length / 2)];

  return (
    <>
      <BaseEdge id={id} path={path} style={style} markerEnd={markerEnd} />
      {label && (
        <EdgeLabelRenderer>
          <div
            className="nodrag nopan"
            style={{
              position: 'absolute',
              transform: `translate(-50%, -50%) translate(${labelPoint.x}px, ${labelPoint.y}px)`,
              pointerEvents: 'all',
              padding: labelBgPadding ? `${labelBgPadding[1]}px ${labelBgPadding[0]}px` : '2px 4px',
              borderRadius: labelBgBorderRadius ?? 4,
              fontSize: 11,
              fontFamily: 'monospace',
              color: (labelStyle?.fill as string) ?? 'var(--color-edge-hover)',
              background: (labelBgStyle?.fill as string) ?? 'var(--color-surface-card)',
              whiteSpace: 'nowrap',
            }}
          >
            {label}
          </div>
        </EdgeLabelRenderer>
      )}
    </>
  );
}

/**
 * Roteamento ortogonal client-side. Reto quando os handles estão alinhados;
 * Z-shape via midpoint vertical quando não estão. Assume target em
 * `Position.Top` (caso dominante no CFG); pra outras posições do target faz
 * um L-shape simples.
 */
function addOrthoBends(
  points: { x: number; y: number }[],
  sx: number, sy: number, sourcePos: Position,
  tx: number, ty: number, targetPos: Position,
) {
  const ALIGN_TOLERANCE = 2;
  const targetIsTop = targetPos === Position.Top;

  if (sourcePos === Position.Bottom || sourcePos === Position.Top) {
    // Saída vertical. Se o target também é vertical (top) e os X batem,
    // desenha uma linha reta sem bends.
    if (targetIsTop && Math.abs(sx - tx) < ALIGN_TOLERANCE) return;
    const midY = (sy + ty) / 2;
    points.push({ x: sx, y: midY });
    points.push({ x: tx, y: midY });
    return;
  }

  // Source em Left/Right — primeiro passo perpendicular ao handle.
  const exitX = sourcePos === Position.Right ? sx + HANDLE_CLEARANCE : sx - HANDLE_CLEARANCE;
  points.push({ x: exitX, y: sy });

  if (targetIsTop) {
    // Se a saída lateral já cai na vertical do target, basta descer reto.
    if (Math.abs(exitX - tx) < ALIGN_TOLERANCE) return;
    const midY = (sy + ty) / 2;
    points.push({ x: exitX, y: midY });
    points.push({ x: tx, y: midY });
  } else {
    // Target lateral (ex.: `loopback` do LoopNode): L-shape descendo até a
    // altura do handle e depois atravessando horizontalmente.
    if (Math.abs(sy - ty) > ALIGN_TOLERANCE) points.push({ x: exitX, y: ty });
  }
}
