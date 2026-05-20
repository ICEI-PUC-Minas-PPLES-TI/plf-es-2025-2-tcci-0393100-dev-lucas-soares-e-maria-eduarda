import ELK, { type ElkNode, type ElkPort } from 'elkjs/lib/elk.bundled.js';
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
// Layout ELK (Eclipse Layout Kernel — layered/Sugiyama)
// ──────────────────────────────────────────────

interface Position {
  x: number;
  y: number;
}

// Dimensões por tipo. Diferem para refletir o shape real renderizado em
// `src/features/graph/components/nodes/*` — assim o ELK não empurra nós
// uns sobre os outros nem desperdiça espaço.
const NODE_SIZE: Record<GFCNodeType, { width: number; height: number }> = {
  START:     { width: 130, height: 44 },
  END:       { width: 110, height: 44 },
  STATEMENT: { width: 200, height: 70 },
  DECISION:  { width: 180, height: 100 },
  TERNARY:   { width: 150, height: 80 },
  LOOP:      { width: 200, height: 90 },
  SWITCH:    { width: 210, height: 90 },
  CASE:      { width: 200, height: 70 },
  CASE_BLOCK:{ width: 220, height: 90 },
  TRY:       { width: 200, height: 70 },
  CATCH:     { width: 200, height: 70 },
  FINALLY:   { width: 200, height: 70 },
  RETURN:    { width: 200, height: 70 },
  BREAK:     { width: 160, height: 50 },
  CONTINUE:  { width: 160, height: 50 },
  THROW:     { width: 160, height: 50 },
};

const ELK_OPTIONS: Record<string, string> = {
  'elk.algorithm': 'layered',
  'elk.direction': 'DOWN',
  'elk.layered.spacing.nodeNodeBetweenLayers': '90',
  'elk.spacing.nodeNode': '60',
  'elk.layered.crossingMinimization.strategy': 'LAYER_SWEEP',
  'elk.layered.cycleBreaking.strategy': 'GREEDY',
  'elk.layered.nodePlacement.strategy': 'NETWORK_SIMPLEX',
  'elk.edgeRouting': 'ORTHOGONAL',
  // Respeita o lado das portas declaradas em cada nó (decisão/loop/etc).
  'elk.portConstraints': 'FIXED_SIDE',
};

const elk = new ELK();

// Port IDs no ELK precisam ser únicos em todo o grafo, não só por nó.
function portId(nodeCode: string, handle: string): string {
  return `${nodeCode}__${handle}`;
}

/**
 * Declara as portas (com lado fixo) que cada tipo de nó expõe para o ELK.
 * Os IDs batem com os `id` dos `<Handle>` dos componentes em
 * `src/features/graph/components/nodes/*`, então o roteamento do React Flow
 * e a colocação do ELK ficam alinhados.
 */
function portsForNode(nodeCode: string, type: GFCNodeType): ElkPort[] {
  const ports: ElkPort[] = [];
  const add = (handle: string, side: 'NORTH' | 'SOUTH' | 'EAST' | 'WEST') => {
    ports.push({
      id: portId(nodeCode, handle),
      layoutOptions: { 'elk.port.side': side },
    });
  };

  if (type !== 'START') add('top', 'NORTH');
  if (type !== 'END') add('bottom', 'SOUTH');

  if (type === 'DECISION' || type === 'TERNARY') {
    add('left', 'WEST');
    add('right', 'EAST');
  }
  if (type === 'LOOP') {
    add('left', 'WEST');
    add('right', 'EAST');
    add('loopback', 'WEST');
  }
  if (type === 'CASE' || type === 'CASE_BLOCK' || type === 'TRY' || type === 'CATCH' || type === 'FINALLY') {
    add('left', 'WEST');
    add('right', 'EAST');
  }
  if (type === 'BREAK' || type === 'CONTINUE' || type === 'THROW') {
    add('left', 'WEST');
    add('right', 'EAST');
  }

  return ports;
}

/**
 * Calcula posições usando ELK (algoritmo layered).
 * Roda em web worker dentro do elkjs (não bloqueia a UI).
 * Trata ciclos via `cycleBreaking: GREEDY` (loop_back/continue/break_flow ficam corretos).
 * Retorna coordenadas top-left, prontas para o React Flow.
 */
export async function computeELKLayout(dto: GFCDTO): Promise<Record<string, Position>> {
  const positions: Record<string, Position> = {};
  if (dto.nodes.length === 0) return positions;

  const children: ElkNode[] = dto.nodes.map((n) => {
    const size = NODE_SIZE[n.type] ?? { width: 200, height: 70 };
    const nodeOptions: Record<string, string> = {};
    // START/END são âncoras: fixá-los na primeira/última camada evita que o
    // ELK posicione o INICIO no meio do grafo quando há ciclos (loops/back-edges).
    if (n.type === 'START') {
      nodeOptions['elk.layered.layering.layerConstraint'] = 'FIRST_SEPARATE';
    } else if (n.type === 'END') {
      nodeOptions['elk.layered.layering.layerConstraint'] = 'LAST_SEPARATE';
    }
    return {
      id: n.code,
      width: size.width,
      height: size.height,
      ports: portsForNode(n.code, n.type),
      layoutOptions: nodeOptions,
    };
  });

  const elkEdges = dto.edges.map((e, idx) => ({
    id: `e${idx}`,
    sources: [portId(e.sourceNodeCode, elkSourceHandleFor(e.type))],
    targets: [portId(e.targetNodeCode, targetHandleFor(e.type))],
  }));

  try {
    const layout = await elk.layout({
      id: 'root',
      layoutOptions: ELK_OPTIONS,
      children,
      edges: elkEdges,
    });
    layout.children?.forEach((c) => {
      positions[c.id] = { x: c.x ?? 0, y: c.y ?? 0 };
    });
  } catch {
    // Fallback: tudo em 0,0 — usuário pode arrastar manualmente.
    dto.nodes.forEach((n) => {
      positions[n.code] = { x: 0, y: 0 };
    });
  }

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
  LOOP: 'loop',
  RETURN: 'return',
  BREAK: 'break',
  CONTINUE: 'continue',
  THROW: 'throw',
  SWITCH: 'switch',
  CASE: 'case',
  CASE_BLOCK: 'caseBlock',
  TRY: 'try',
  CATCH: 'catch',
  FINALLY: 'finally',
  TERNARY: 'ternary',
};

/**
 * Calcula posições (via ELK ou localStorage) e monta nodes + edges juntos.
 * Combinar é necessário porque o handle de saída de arestas de branch é decidido
 * pela posição relativa source/target — o ELK pode colocar o alvo do TRUE à
 * direita ou à esquerda do losango e queremos que a aresta sempre saia do lado certo.
 */
export async function buildFlowGraph(
  dto: GFCDTO,
): Promise<{ nodes: GFCFlowNode[]; edges: GFCFlowEdge[] }> {
  const saved = loadPositions(dto.id);
  const allSaved = dto.nodes.every((n) => saved[n.code] != null);
  const computed = allSaved ? {} : await computeELKLayout(dto);

  const positions: Record<string, Position> = {};
  dto.nodes.forEach((n) => {
    positions[n.code] = saved[n.code] ?? computed[n.code] ?? { x: 0, y: 0 };
  });

  const nodes: GFCFlowNode[] = dto.nodes.map((node) => ({
    id: node.code,
    type: NODE_TYPE_TO_KIND[node.type],
    position: positions[node.code],
    data: {
      code: node.code,
      label: node.label,
      nodeType: node.type,
      startLine: node.startLine,
      endLine: node.endLine,
    },
  }));

  const edges: GFCFlowEdge[] = dto.edges.map((edge) => {
    const src = positions[edge.sourceNodeCode] ?? { x: 0, y: 0 };
    const tgt = positions[edge.targetNodeCode] ?? { x: 0, y: 0 };
    return {
      id: edge.id,
      source: edge.sourceNodeCode,
      target: edge.targetNodeCode,
      sourceHandle: sourceHandleFor(edge.type, src, tgt),
      targetHandle: targetHandleFor(edge.type),
      type: 'smoothstep',
      label: edgeLabel(edge.type, edge.label),
      data: {
        edgeType: edge.type,
        backendId: edge.id,
        label: edge.label,
      },
    };
  });

  return { nodes, edges };
}

/**
 * Versão declarativa usada para alimentar o ELK (que ainda não conhece posições).
 * Dá ao ELK uma dica de qual lado da decisão/loop o branch sai, mesmo que o
 * resultado final possa ser ajustado em runtime por `sourceHandleFor`.
 */
function elkSourceHandleFor(type: GFCEdgeType): string {
  switch (type) {
    case 'TRUE_BRANCH': return 'left';
    case 'FALSE_BRANCH': return 'right';
    case 'CATCH_BRANCH': return 'right';
    case 'THROW_FLOW': return 'right';
    case 'BREAK_FLOW': return 'right';
    case 'CONTINUE_FLOW': return 'left';
    default: return 'bottom';
  }
}

function targetHandleFor(type: GFCEdgeType): string {
  // Back-edges entram pelo handle dedicado do LoopNode para curvar pelo lado,
  // em vez de empilhar no `top` junto da entrada principal do loop.
  if (type === 'LOOP_BACK' || type === 'CONTINUE_FLOW') return 'loopback';
  return 'top';
}

/**
 * Escolhe a porta de saída baseada na posição relativa do alvo. Para branches
 * (true/false, break, throw, etc.) o ELK pode colocar o alvo de qualquer lado;
 * pegar a porta correspondente evita que a aresta cruze por cima do losango.
 */
function sourceHandleFor(type: GFCEdgeType, src: Position, tgt: Position): string {
  const HORIZ_TOLERANCE = 20;
  const onRight = tgt.x > src.x + HORIZ_TOLERANCE;
  const onLeft = tgt.x < src.x - HORIZ_TOLERANCE;

  switch (type) {
    case 'TRUE_BRANCH':
      return onRight ? 'right' : 'left';
    case 'FALSE_BRANCH':
      return onLeft ? 'left' : 'right';
    case 'CATCH_BRANCH':
    case 'THROW_FLOW':
    case 'BREAK_FLOW':
      return onLeft ? 'left' : onRight ? 'right' : 'bottom';
    case 'CONTINUE_FLOW':
      return onRight ? 'right' : 'left';
    case 'LOOP_BACK':
    case 'LOOP_BODY':
    case 'LOOP_EXIT':
    case 'FINALLY_BRANCH':
      return 'bottom';
    default:
      return 'bottom';
  }
}

function edgeLabel(type: GFCEdgeType, raw: string | null): string {
  if (raw && raw.length > 0) return raw;
  switch (type) {
    case 'TRUE_BRANCH': return 'true';
    case 'FALSE_BRANCH': return 'false';
    case 'LOOP_BACK': return 'loop';
    case 'LOOP_BODY': return 'body';
    case 'LOOP_EXIT': return 'exit';
    case 'DEFAULT_BRANCH': return 'default';
    case 'TRY_BRANCH': return 'try';
    case 'CATCH_BRANCH': return 'catch';
    case 'FINALLY_BRANCH': return 'finally';
    case 'BREAK_FLOW': return 'break';
    case 'CONTINUE_FLOW': return 'continue';
    case 'THROW_FLOW': return 'throw';
    default: return '';
  }
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
