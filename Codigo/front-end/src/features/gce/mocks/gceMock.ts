import type { GCEDTO, GCEFlowNode, GCEFlowEdge, GCERestriction } from '../types/gce';

export const GCE_MOCK: GCEDTO = {
  id: 'gce-001',
  projectId: '794c4113-0ac8-4036-b765-3d45d60309ae',
  name: 'GCE de saque',
  description: 'Fluxo de validacao de saque',
  selected: true,
  nodes: [
    { code: 'C1', label: 'Usuario autenticado', type: 'CAUSE', operatorType: null },
    { code: 'C2', label: 'Saldo suficiente', type: 'CAUSE', operatorType: null },
    { code: 'O1', label: 'C1 AND C2', type: 'OPERATOR', operatorType: 'AND' },
    { code: 'E1', label: 'Permitir saque', type: 'EFFECT', operatorType: null },
  ],
  edges: [
    { sourceNodeCode: 'C1', targetNodeCode: 'O1', type: 'IDENTITY' },
    { sourceNodeCode: 'C2', targetNodeCode: 'O1', type: 'IDENTITY' },
    { sourceNodeCode: 'O1', targetNodeCode: 'E1', type: 'IDENTITY' },
  ],
  restrictions: [
    { type: 'EXCLUSIVE', nodeCodes: ['C1', 'C2'] },
  ],
};

const NODE_POSITIONS: Record<string, { x: number; y: number }> = {
  C1: { x: 50, y: 50 },
  C2: { x: 50, y: 200 },
  O1: { x: 350, y: 125 },
  E1: { x: 650, y: 125 },
};

export function dtoToFlowNodes(dto: GCEDTO): GCEFlowNode[] {
  return dto.nodes.map((node) => {
    const typeMap = { CAUSE: 'cause', EFFECT: 'effect', OPERATOR: 'operator' } as const;
    return {
      id: node.code,
      type: typeMap[node.type],
      position: NODE_POSITIONS[node.code] ?? { x: 0, y: 0 },
      data: {
        code: node.code,
        label: node.label,
        nodeType: node.type,
        operatorType: node.operatorType,
      },
    };
  });
}

export function dtoToFlowEdges(dto: GCEDTO): GCEFlowEdge[] {
  return dto.edges.map((edge, i) => ({
    id: `e-${i}`,
    source: edge.sourceNodeCode,
    target: edge.targetNodeCode,
    type: edge.type === 'NEGATION' ? 'negation' : 'default',
    data: { edgeType: edge.type },
  }));
}

export function dtoToRestrictions(dto: GCEDTO): GCERestriction[] {
  return dto.restrictions.map((r) => ({ type: r.type, nodeCodes: r.nodeCodes }));
}
