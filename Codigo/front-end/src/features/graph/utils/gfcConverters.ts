import dagre from '@dagrejs/dagre';
import type {
  GFCDTO,
  GFCFlowNode,
  GFCFlowEdge,
  GFCFlowNodeKind,
  GFCNodeType,
  GFCEdgeType,
} from '../types/gfc';

// ──────────────────────────────────────────────
// Persistência local de posições (mesmo padrão do GCE)
// ──────────────────────────────────────────────

function positionsKey(gfcId: string) {
  return `gfc_positions_${gfcId}`;
}

export function loadPositions(gfcId: string): Record<string, { x: number; y: number }> {
  try {
    return JSON.parse(localStorage.getItem(positionsKey(gfcId)) ?? '{}');
  } catch {
    return {};
  }
}

export function savePositions(gfcId: string, nodes: GFCFlowNode[]) {
  const positions: Record<string, { x: number; y: number }> = {};
  nodes.forEach((n) => {
    positions[n.id] = n.position;
  });
  localStorage.setItem(positionsKey(gfcId), JSON.stringify(positions));
}

// ──────────────────────────────────────────────
// Layout Dagre (Sugiyama / layered)
// ──────────────────────────────────────────────

const NODE_WIDTH = 200;
const NODE_HEIGHT = 80;

interface Position {
  x: number;
  y: number;
}

/**
 * Calcula posições usando @dagrejs/dagre (algoritmo de layered/Sugiyama).
 * Trata ciclos via `acyclicer: 'greedy'` (back-edges como LOOP_BACK não causam crossing).
 * Dagre devolve coordenadas do centro do nó; converte para top-left esperado pelo React Flow.
 */
export function computeDagreLayout(dto: GFCDTO): Record<string, Position> {
  const positions: Record<string, Position> = {};
  if (dto.nodes.length === 0) return positions;

  const g = new dagre.graphlib.Graph();
  g.setGraph({
    rankdir: 'TB',
    nodesep: 50,
    ranksep: 70,
    acyclicer: 'greedy',
    ranker: 'network-simplex',
  });
  g.setDefaultEdgeLabel(() => ({}));

  dto.nodes.forEach((n) => {
    g.setNode(n.code, { width: NODE_WIDTH, height: NODE_HEIGHT });
  });
  dto.edges.forEach((e) => {
    g.setEdge(e.sourceNodeCode, e.targetNodeCode);
  });

  dagre.layout(g);

  dto.nodes.forEach((n) => {
    const node = g.node(n.code);
    if (!node) {
      positions[n.code] = { x: 0, y: 0 };
      return;
    }
    positions[n.code] = {
      x: node.x - NODE_WIDTH / 2,
      y: node.y - NODE_HEIGHT / 2,
    };
  });

  return positions;
}

// ──────────────────────────────────────────────
// DTO → React Flow
// ──────────────────────────────────────────────

const NODE_TYPE_TO_KIND: Record<GFCNodeType, GFCFlowNodeKind> = {
  START: 'start',
  END: 'end',
  STATEMENT: 'statement',
  DECISION: 'decision',
  RETURN: 'return',
};

export function dtoToFlowNodes(dto: GFCDTO): GFCFlowNode[] {
  const saved = loadPositions(dto.id);
  const computed = computeDagreLayout(dto);
  return dto.nodes.map((node) => ({
    id: node.code,
    type: NODE_TYPE_TO_KIND[node.type],
    position: saved[node.code] ?? computed[node.code] ?? { x: 0, y: 0 },
    data: {
      code: node.code,
      label: node.label,
      nodeType: node.type,
      startLine: node.startLine,
      endLine: node.endLine,
    },
  }));
}

export function dtoToFlowEdges(dto: GFCDTO): GFCFlowEdge[] {
  return dto.edges.map((edge) => ({
    id: edge.id,
    source: edge.sourceNodeCode,
    target: edge.targetNodeCode,
    sourceHandle: sourceHandleFor(edge.type),
    targetHandle: 'top',
    type: 'smoothstep',
    label: edgeLabel(edge.type, edge.label),
    data: {
      edgeType: edge.type,
      backendId: edge.id,
      label: edge.label,
    },
  }));
}

/**
 * Para o nó DECISÃO, escolhe o handle de saída com base no tipo de aresta.
 * - TRUE_BRANCH → handle esquerdo (verde)
 * - FALSE_BRANCH → handle direito (vermelho)
 * - LOOP_BACK → handle direito também (mais comum em loops "do/while")
 * - SEQUENTIAL e demais → handle inferior (default)
 */
function sourceHandleFor(type: GFCEdgeType): string {
  if (type === 'TRUE_BRANCH') return 'left';
  if (type === 'FALSE_BRANCH') return 'right';
  if (type === 'LOOP_BACK') return 'right';
  return 'bottom';
}

function edgeLabel(type: GFCEdgeType, raw: string | null): string {
  if (raw && raw.length > 0) return raw;
  if (type === 'TRUE_BRANCH') return 'true';
  if (type === 'FALSE_BRANCH') return 'false';
  if (type === 'LOOP_BACK') return 'loop';
  return '';
}

// ──────────────────────────────────────────────
// Estatísticas (calculadas no client)
// ──────────────────────────────────────────────

export interface GFCStats {
  nodeCount: number;
  edgeCount: number;
  cyclomaticComplexity: number;
}

/**
 * Complexidade ciclomática de McCabe: M = E - N + 2P (P=1 componente conexo).
 * Para grafos não-conexos cai para M = E - N + 2, que continua sendo um limite inferior útil.
 */
export function computeStats(dto: GFCDTO): GFCStats {
  const n = dto.nodes.length;
  const e = dto.edges.length;
  return {
    nodeCount: n,
    edgeCount: e,
    cyclomaticComplexity: n === 0 ? 0 : Math.max(1, e - n + 2),
  };
}
