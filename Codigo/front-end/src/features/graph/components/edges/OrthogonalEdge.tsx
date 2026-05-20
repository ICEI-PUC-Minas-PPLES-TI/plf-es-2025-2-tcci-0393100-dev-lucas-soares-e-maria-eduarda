import { BaseEdge, EdgeLabelRenderer, type EdgeProps } from '@xyflow/react';
import type { GFCFlowEdgeData } from '../../types/gfc';

/**
 * Aresta ortogonal que segue os bend points calculados pelo ELK. Comparada
 * com `smoothstep`, evita cruzar nós e empilhar arestas paralelas porque o
 * ELK posiciona cada uma em um "canal" distinto entre as camadas.
 *
 * Quando o nó é arrastado pelo usuário, os bend points ficam desalinhados.
 * Aceitamos esse trade-off por enquanto: limpar `localStorage` recomputa.
 */
export function OrthogonalEdge(props: EdgeProps) {
  const { id, sourceX, sourceY, targetX, targetY, style, markerEnd, label, labelStyle, labelBgStyle, labelBgPadding, labelBgBorderRadius } = props;
  const data = props.data as GFCFlowEdgeData | undefined;
  const bends = data?.bendPoints ?? [];

  // Conecta source/handle reais ao caminho ortogonal do ELK; se não houver
  // bends, vai direto em linha reta.
  const points: { x: number; y: number }[] = [{ x: sourceX, y: sourceY }];
  if (bends.length > 0) {
    // O primeiro/último ponto do ELK pode estar levemente deslocado em relação
    // ao handle que o React Flow renderiza; pular eles e ligar os bends do meio
    // aos handles reais evita "dente" visível na ponta.
    bends.slice(1, -1).forEach((p) => points.push(p));
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
