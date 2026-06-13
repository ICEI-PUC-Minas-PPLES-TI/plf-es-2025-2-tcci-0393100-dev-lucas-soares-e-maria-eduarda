import ELK from 'elkjs/lib/elk.bundled.js';
import type { GCEDTO, GCEFlowNode, GCEFlowEdge, GCERestriction, CreateGCERequest, GCENodeType } from '../types/gce';

// ──────────────────────────────────────────────────────────────
// Node positions: stored in localStorage keyed by gceId
// ──────────────────────────────────────────────────────────────

function positionsKey(gceId: string) {
  return `gce_positions_${gceId}`;
}

function bendsKey(gceId: string) {
  return `gce_bends_${gceId}`;
}

interface EdgeMeta {
  bend?: { x: number; y: number };
  sourceHandle?: string;
  targetHandle?: string;
}

export function loadBends(gceId: string): Record<string, EdgeMeta> {
  try {
    return JSON.parse(localStorage.getItem(bendsKey(gceId)) ?? '{}');
  } catch {
    return {};
  }
}

export function saveBends(gceId: string, edges: GCEFlowEdge[]) {
  const meta: Record<string, EdgeMeta> = {};
  edges.forEach((e) => {
    const key = `${e.source}-${e.target}`;
    const entry: EdgeMeta = {};
    if (e.data?.bend) entry.bend = e.data.bend;
    if (e.sourceHandle) entry.sourceHandle = e.sourceHandle;
    if (e.targetHandle) entry.targetHandle = e.targetHandle;
    meta[key] = entry;
  });
  localStorage.setItem(bendsKey(gceId), JSON.stringify(meta));
}

export function loadPositions(gceId: string): Record<string, { x: number; y: number }> {
  try {
    return JSON.parse(localStorage.getItem(positionsKey(gceId)) ?? '{}');
  } catch {
    return {};
  }
}

export function savePositions(gceId: string, nodes: GCEFlowNode[]) {
  const positions: Record<string, { x: number; y: number }> = {};
  nodes.forEach((n) => { positions[n.id] = n.position; });
  localStorage.setItem(positionsKey(gceId), JSON.stringify(positions));
}

/** Apaga as posições manuais salvas para forçar o auto-layout a recalcular. */
export function clearPositions(gceId: string) {
  localStorage.removeItem(positionsKey(gceId));
}

/** Apaga os bend points salvos (ficam órfãos quando o layout é recalculado). */
export function clearBends(gceId: string) {
  localStorage.removeItem(bendsKey(gceId));
}

// ──────────────────────────────────────────────────────────────
// Auto-layout (ELK — algoritmo layered/Sugiyama)
//
// As posições dos nós só vivem no localStorage (o backend não as guarda).
// Quando um GCE é aberto numa máquina/navegador que ainda não tem posições
// salvas, calculamos um layout em camadas da esquerda para a direita
// (causas → operadores → efeito) em vez de jogar tudo num grid que ignora
// as conexões e deixa as arestas cruzando todo o canvas.
// ──────────────────────────────────────────────────────────────

const elk = new ELK();

// Tamanhos aproximados dos nós (ver CauseNode/EffectNode/OperatorNode) — o ELK
// usa só para reservar espaço e não sobrepor os nós.
const NODE_SIZE: Record<GCENodeType, { width: number; height: number }> = {
  CAUSE: { width: 190, height: 52 },
  EFFECT: { width: 190, height: 52 },
  OPERATOR: { width: 80, height: 60 },
};

const ELK_OPTIONS: Record<string, string> = {
  'elk.algorithm': 'layered',
  'elk.direction': 'RIGHT',
  'elk.layered.spacing.nodeNodeBetweenLayers': '120',
  'elk.spacing.nodeNode': '50',
  'elk.layered.crossingMinimization.strategy': 'LAYER_SWEEP',
  'elk.layered.nodePlacement.strategy': 'NETWORK_SIMPLEX',
};

export async function computeGCELayout(dto: GCEDTO): Promise<Record<string, { x: number; y: number }>> {
  if (dto.nodes.length === 0) return {};

  const layout = await elk.layout({
    id: 'root',
    layoutOptions: ELK_OPTIONS,
    children: dto.nodes.map((n) => ({ id: n.code, ...NODE_SIZE[n.type] })),
    edges: dto.edges.map((e, i) => ({
      id: `e${i}`,
      sources: [e.sourceNodeCode],
      targets: [e.targetNodeCode],
    })),
  });

  const positions: Record<string, { x: number; y: number }> = {};
  layout.children?.forEach((c) => { positions[c.id] = { x: c.x ?? 0, y: c.y ?? 0 }; });
  return positions;
}

// ──────────────────────────────────────────────────────────────
// DTO → Flow (used when loading a GCE)
// ──────────────────────────────────────────────────────────────

/**
 * Resolve a posição de cada nó. Posições salvas no localStorage têm prioridade;
 * o ELK só roda quando algum nó ainda não tem posição salva (GCE novo, aberto
 * em outra máquina, ou após limpar o cache). `fresh` indica que o layout foi
 * recalculado pelo ELK — nesse caso os bend points salvos ficam órfãos e as
 * arestas devem ser desenhadas do zero (ver dtoToFlowEdges).
 */
async function resolvePositions(dto: GCEDTO): Promise<{ positions: Record<string, { x: number; y: number }>; fresh: boolean }> {
  const saved = loadPositions(dto.id);
  const allSaved = dto.nodes.length > 0 && dto.nodes.every((n) => saved[n.code] != null);
  if (allSaved) return { positions: saved, fresh: false };

  const layout = await computeGCELayout(dto);
  const positions: Record<string, { x: number; y: number }> = {};
  dto.nodes.forEach((n) => { positions[n.code] = saved[n.code] ?? layout[n.code] ?? { x: 0, y: 0 }; });
  return { positions, fresh: true };
}

function buildNodes(dto: GCEDTO, positions: Record<string, { x: number; y: number }>): GCEFlowNode[] {
  const typeMap = { CAUSE: 'cause', EFFECT: 'effect', OPERATOR: 'operator' } as const;
  return dto.nodes.map((node) => ({
    id: node.code,
    type: typeMap[node.type],
    position: positions[node.code] ?? { x: 0, y: 0 },
    data: {
      code: node.code,
      label: node.label ?? '',
      nodeType: node.type,
      operatorType: node.operatorType ?? null,
    },
  }));
}

export async function dtoToFlowNodes(dto: GCEDTO): Promise<GCEFlowNode[]> {
  const { positions } = await resolvePositions(dto);
  return buildNodes(dto, positions);
}

/**
 * `freshLayout` = o layout foi recalculado pelo ELK. Nesse caso ignoramos os
 * bend points salvos (que apontariam para as posições antigas dos nós e fariam
 * a aresta dar voltas enormes) e fixamos os handles no fluxo esquerda → direita:
 * sai pela direita do source, entra pela esquerda do target.
 */
export function dtoToFlowEdges(dto: GCEDTO, freshLayout = false): GCEFlowEdge[] {
  const bends = freshLayout ? {} : loadBends(dto.id);
  return dto.edges.map((edge, i) => {
    const meta = bends[`${edge.sourceNodeCode}-${edge.targetNodeCode}`];
    return {
      id: `e-${i}`,
      source: edge.sourceNodeCode,
      target: edge.targetNodeCode,
      sourceHandle: meta?.sourceHandle ?? (freshLayout ? 'right' : undefined),
      targetHandle: meta?.targetHandle ?? (freshLayout ? 'left' : undefined),
      type: edge.type === 'NEGATED' ? 'negation' : 'editable',
      data: {
        edgeType: edge.type,
        backendId: edge.id,
        bend: meta?.bend,
      },
    };
  });
}

/**
 * Monta nós + arestas + restrições de forma consistente: a decisão "usar
 * posições salvas vs. recalcular com ELK" é tomada uma vez só e aplicada tanto
 * aos nós quanto às arestas, evitando o descasamento de posições novas com
 * bends antigos.
 */
export async function buildFlow(dto: GCEDTO): Promise<{ nodes: GCEFlowNode[]; edges: GCEFlowEdge[]; restrictions: GCERestriction[] }> {
  const { positions, fresh } = await resolvePositions(dto);
  return {
    nodes: buildNodes(dto, positions),
    edges: dtoToFlowEdges(dto, fresh),
    restrictions: dtoToRestrictions(dto),
  };
}

export function dtoToRestrictions(dto: GCEDTO): GCERestriction[] {
  return dto.restrictions.map((r) => ({
    id: r.id,
    type: r.type,
    nodeCodes: r.nodeCodes,
  }));
}

// ──────────────────────────────────────────────────────────────
// Flow → DTO (used when saving)
// ──────────────────────────────────────────────────────────────

export function flowToCreateRequest(
  base: { projectId: string; name: string; description: string; selected: boolean },
  nodes: GCEFlowNode[],
  edges: GCEFlowEdge[],
  restrictions: GCERestriction[],
): CreateGCERequest {
  return {
    projectId: base.projectId,
    name: base.name,
    description: base.description,
    selected: base.selected,
    nodes: nodes.map((n) => ({
      code: n.data.code,
      ...(n.data.nodeType !== 'OPERATOR' && { label: n.data.label }),
      type: n.data.nodeType,
      ...(n.data.nodeType === 'OPERATOR' && { operatorType: n.data.operatorType }),
    })),
    edges: edges.map((e) => ({
      sourceNodeCode: e.source,
      targetNodeCode: e.target,
      type: e.data?.edgeType ?? 'IDENTITY',
    })),
    restrictions: restrictions.map((r) => ({
      type: r.type,
      nodeCodes: r.nodeCodes,
    })),
  };
}
