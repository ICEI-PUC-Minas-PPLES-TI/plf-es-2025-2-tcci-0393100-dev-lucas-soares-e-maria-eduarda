import type { GCEDTO } from '../types/gce';

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

export { dtoToFlowNodes, dtoToFlowEdges, dtoToRestrictions } from '../utils/gceConverters';
