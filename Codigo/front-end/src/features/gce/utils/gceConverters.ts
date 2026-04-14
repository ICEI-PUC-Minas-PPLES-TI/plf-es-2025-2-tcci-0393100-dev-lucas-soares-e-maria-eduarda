import type { GCEDTO, GCEFlowNode, GCEFlowEdge, GCERestriction, CreateGCERequest } from '../types/gce';

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

// ──────────────────────────────────────────────────────────────
// DTO → Flow (used when loading a GCE)
// ──────────────────────────────────────────────────────────────

export function dtoToFlowNodes(dto: GCEDTO): GCEFlowNode[] {
  const saved = loadPositions(dto.id);
  return dto.nodes.map((node, index) => {
    const typeMap = { CAUSE: 'cause', EFFECT: 'effect', OPERATOR: 'operator' } as const;
    return {
      id: node.code,
      type: typeMap[node.type],
      position: saved[node.code] ?? { x: (index % 3) * 260, y: Math.floor(index / 3) * 160 },
      data: {
        code: node.code,
        label: node.label ?? '',
        nodeType: node.type,
        operatorType: node.operatorType,
      },
    };
  });
}

export function dtoToFlowEdges(dto: GCEDTO): GCEFlowEdge[] {
  const bends = loadBends(dto.id);
  return dto.edges.map((edge, i) => {
    const meta = bends[`${edge.sourceNodeCode}-${edge.targetNodeCode}`];
    return {
      id: `e-${i}`,
      source: edge.sourceNodeCode,
      target: edge.targetNodeCode,
      sourceHandle: meta?.sourceHandle,
      targetHandle: meta?.targetHandle,
      type: edge.type === 'NEGATED' ? 'negation' : 'editable',
      data: {
        edgeType: edge.type,
        backendId: edge.id,
        bend: meta?.bend,
      },
    };
  });
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
      operatorType: n.data.operatorType,
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
