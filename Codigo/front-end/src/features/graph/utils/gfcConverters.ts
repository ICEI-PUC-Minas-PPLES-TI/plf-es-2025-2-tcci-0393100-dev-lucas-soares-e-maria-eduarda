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

/** Apaga as posições manuais salvas para forçar o ELK a recalcular o layout. */
export function clearPositions(gfcId: string) {
  localStorage.removeItem(positionsKey(gfcId));
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
  'elk.spacing.nodeNode': '80',
  // Reserva folga lateral entre os canais de aresta e os nós da camada vizinha.
  // Sem isso o ELK cola o canal vertical de uma `FALSE_BRANCH` (que atravessa
  // a camada do `THEN`) na borda do nó do `THEN`, e visualmente parece que a
  // aresta passa por cima do nó.
  'elk.spacing.edgeNode': '35',
  'elk.layered.spacing.edgeNodeBetweenLayers': '40',
  'elk.layered.spacing.edgeEdgeBetweenLayers': '20',
  'elk.layered.crossingMinimization.strategy': 'LAYER_SWEEP',
  // DEPTH_FIRST faz DFS a partir do START: qualquer aresta que volte pra ancestral
  // é marcada como back-edge. Pra CFGs (que sempre têm uma entrada clara), é mais
  // confiável que GREEDY — esse último heurístico às vezes reverte uma forward-edge
  // (ex.: LOOP_BODY) quando o nó LOOP tem várias entradas (SEQUENTIAL + LOOP_BACK + CONTINUE_FLOW).
  'elk.layered.cycleBreaking.strategy': 'DEPTH_FIRST',
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

interface ELKLayoutResult {
  positions: Record<string, Position>;
  /** Bend points (cantos do roteamento ortogonal) por id de aresta do DTO. */
  bendPoints: Record<string, Position[]>;
}

/**
 * Calcula posições usando ELK (algoritmo layered).
 * Roda em web worker dentro do elkjs (não bloqueia a UI).
 * Trata ciclos via `cycleBreaking: GREEDY` (loop_back/continue/break_flow ficam corretos).
 * Retorna coordenadas top-left dos nós + bend points por aresta (usados pelo
 * `OrthogonalEdge` pra evitar cruzamentos).
 */
export async function computeELKLayout(dto: GFCDTO): Promise<ELKLayoutResult> {
  const positions: Record<string, Position> = {};
  const bendPoints: Record<string, Position[]> = {};
  if (dto.nodes.length === 0) return { positions, bendPoints };

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

  // Back-edges semânticas: marcadas com prioridade baixa pra serem revertidas
  // na fase de cycle breaking. Sem isso o GREEDY às vezes reverte uma forward-edge
  // (ex.: `LOOP_BODY`) e o nó LOOP termina abaixo do próprio corpo.
  const BACK_EDGE_TYPES: ReadonlySet<GFCEdgeType> = new Set(['LOOP_BACK', 'CONTINUE_FLOW']);

  // Usa o próprio id da aresta (UUID do backend) pra conseguir parear o
  // resultado do ELK com `dto.edges` mantendo o mapa de bend points por edge.
  const elkEdges = dto.edges.map((e) => ({
    id: e.id,
    sources: [portId(e.sourceNodeCode, elkSourceHandleFor(e.type))],
    targets: [portId(e.targetNodeCode, targetHandleFor(e.type))],
    layoutOptions: BACK_EDGE_TYPES.has(e.type)
      ? { 'elk.layered.priority.direction': '-1' }
      : undefined,
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
    layout.edges?.forEach((e) => {
      const section = e.sections?.[0];
      if (!section) return;
      const points: Position[] = [];
      if (section.startPoint) points.push({ x: section.startPoint.x ?? 0, y: section.startPoint.y ?? 0 });
      section.bendPoints?.forEach((p) => points.push({ x: p.x ?? 0, y: p.y ?? 0 }));
      if (section.endPoint) points.push({ x: section.endPoint.x ?? 0, y: section.endPoint.y ?? 0 });
      bendPoints[e.id] = points;
    });
  } catch {
    // Fallback: tudo em 0,0 — usuário pode arrastar manualmente.
    dto.nodes.forEach((n) => {
      positions[n.code] = { x: 0, y: 0 };
    });
  }

  return { positions, bendPoints };
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
  const layout: ELKLayoutResult = allSaved
    ? { positions: {}, bendPoints: {} }
    : await computeELKLayout(dto);

  const positions: Record<string, Position> = {};
  dto.nodes.forEach((n) => {
    positions[n.code] = saved[n.code] ?? layout.positions[n.code] ?? { x: 0, y: 0 };
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
    const bendPoints = layout.bendPoints[edge.id] ?? [];
    return {
      id: edge.id,
      source: edge.sourceNodeCode,
      target: edge.targetNodeCode,
      sourceHandle: sourceHandleFor(edge.type),
      targetHandle: targetHandleFor(edge.type),
      // Usa o roteamento ortogonal do ELK quando disponível; sem bend points cai
      // pro smoothstep do React Flow (ex.: quando todas as posições vieram do localStorage).
      type: bendPoints.length > 0 ? 'orthogonal' : 'smoothstep',
      label: edgeLabel(edge.type, edge.label),
      data: {
        edgeType: edge.type,
        backendId: edge.id,
        label: edge.label,
        bendPoints,
      },
    };
  });

  return { nodes, edges };
}

/**
 * Versão declarativa usada para alimentar o ELK (que ainda não conhece posições).
 * Só forçamos left/right para `TRUE_BRANCH`/`FALSE_BRANCH` porque essas são as
 * únicas que precisam dividir a saída de um losango. Pros outros tipos (break,
 * continue, throw, case) deixamos `bottom` — assim o ELK posiciona o alvo abaixo,
 * permitindo o layout em "leque" do switch/case (todos os bodies em uma fileira,
 * convergindo num merge embaixo). A escolha final do handle visual é feita por
 * `sourceHandleFor` em runtime, baseada na posição relativa.
 */
function elkSourceHandleFor(type: GFCEdgeType): string {
  switch (type) {
    case 'TRUE_BRANCH': return 'left';
    case 'FALSE_BRANCH': return 'right';
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
 * Handle visual do React Flow. Tem que bater exatamente com a porta declarada
 * em `elkSourceHandleFor` — se divergir, `sourceX/sourceY` (posição do handle)
 * fica em um lado do nó enquanto os bend points do ELK começam em outro, e o
 * primeiro segmento da aresta vira uma diagonal cortando o nó.
 *
 * Com `elk.portConstraints: FIXED_SIDE`, o ELK posiciona os alvos respeitando
 * o lado declarado da porta, então não precisamos escolher dinamicamente.
 */
function sourceHandleFor(type: GFCEdgeType): string {
  return elkSourceHandleFor(type);
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
