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
// Layout BFS top-down
// ──────────────────────────────────────────────

const LEVEL_HEIGHT = 120;
const NODE_WIDTH = 200;

interface Position {
  x: number;
  y: number;
}

/**
 * Distribui os nós em níveis usando BFS a partir do nó START.
 * Nós no mesmo nível ficam alinhados horizontalmente, espaçados em torno do x=0.
 * Se não há START, parte do primeiro nó da lista.
 */
export function computeBFSLayout(dto: GFCDTO): Record<string, Position> {
  const positions: Record<string, Position> = {};
  if (dto.nodes.length === 0) return positions;

  const adjacency = new Map<string, string[]>();
  dto.nodes.forEach((n) => adjacency.set(n.code, []));
  dto.edges.forEach((e) => {
    const list = adjacency.get(e.sourceNodeCode);
    if (list && !list.includes(e.targetNodeCode)) list.push(e.targetNodeCode);
  });

  const root = dto.nodes.find((n) => n.type === 'START')?.code ?? dto.nodes[0].code;
  const level = new Map<string, number>();
  const queue: string[] = [root];
  level.set(root, 0);

  while (queue.length > 0) {
    const current = queue.shift()!;
    const currentLevel = level.get(current)!;
    const neighbors = adjacency.get(current) ?? [];
    for (const neighbor of neighbors) {
      if (!level.has(neighbor)) {
        level.set(neighbor, currentLevel + 1);
        queue.push(neighbor);
      }
    }
  }

  // Nós inalcançáveis: empurra para o último nível conhecido + 1
  let maxLevel = 0;
  level.forEach((l) => { if (l > maxLevel) maxLevel = l; });
  dto.nodes.forEach((n) => {
    if (!level.has(n.code)) level.set(n.code, maxLevel + 1);
  });

  // Agrupa por nível
  const byLevel = new Map<number, string[]>();
  level.forEach((l, code) => {
    if (!byLevel.has(l)) byLevel.set(l, []);
    byLevel.get(l)!.push(code);
  });

  // Centraliza cada nível em torno de x=0
  byLevel.forEach((codes, l) => {
    const total = codes.length;
    const startX = -((total - 1) * NODE_WIDTH) / 2;
    codes.forEach((code, idx) => {
      positions[code] = { x: startX + idx * NODE_WIDTH, y: l * LEVEL_HEIGHT };
    });
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
  const computed = computeBFSLayout(dto);
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
    type: 'smoothstep',
    label: edgeLabel(edge.type, edge.label),
    data: {
      edgeType: edge.type,
      backendId: edge.id,
      label: edge.label,
    },
  }));
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
