import { useNodes, useEdges, useReactFlow } from '@xyflow/react';
import type { GCEFlowNode, GCEFlowEdge, GCENodeData, OperatorType, GCERestriction, RestrictionType } from '../types/gce';

interface PropertiesPanelProps {
  selectedNodeId: string | null;
  selectedEdgeId: string | null;
  restrictions?: GCERestriction[];
}

const RESTRICTION_LABEL: Record<RestrictionType, string> = {
  EXCLUSIVE: 'Exclusivo (E)',
  INCLUSIVE: 'Inclusivo (I)',
  ONE_AND_ONLY_ONE: 'Somente um (O)',
  REQUIRE: 'Exige (R)',
  MASKS: 'Mascara (M)',
};

const RESTRICTION_SYMBOL: Record<RestrictionType, string> = {
  EXCLUSIVE: 'E',
  INCLUSIVE: 'I',
  ONE_AND_ONLY_ONE: 'O',
  REQUIRE: 'R',
  MASKS: 'M',
};

export function PropertiesPanel({ selectedNodeId, selectedEdgeId, restrictions = [] }: PropertiesPanelProps) {
  const { setNodes, setEdges } = useReactFlow();
  const nodes = useNodes() as GCEFlowNode[];
  const edges = useEdges() as GCEFlowEdge[];

  const selectedNode = selectedNodeId ? nodes.find((n) => n.id === selectedNodeId) : null;
  const selectedEdge = selectedEdgeId ? edges.find((e) => e.id === selectedEdgeId) : null;

  const updateNodeData = (id: string, updates: Partial<GCENodeData>) => {
    setNodes((nds) =>
      nds.map((n) =>
        n.id === id ? { ...n, data: { ...n.data, ...updates } } : n,
      ),
    );
  };

  const updateEdgeType = (id: string, type: 'default' | 'negation') => {
    setEdges((eds) =>
      eds.map((e) =>
        e.id === id
          ? { ...e, type, data: { ...e.data, edgeType: type === 'negation' ? 'NEGATED' : 'IDENTITY' } }
          : e,
      ),
    );
  };

  if (!selectedNode && !selectedEdge) {
    return (
      <div className="w-72 bg-surface border-l border-edge p-4 shrink-0">
        <h3 className="text-sm text-gray-300 mb-4">Propriedades</h3>
        <p className="text-sm text-gray-500 text-center py-8">
          Selecione um elemento para ver suas propriedades
        </p>
      </div>
    );
  }

  if (selectedNode) {
    const { code, label, nodeType, operatorType } = selectedNode.data;
    const typeLabel = nodeType === 'CAUSE' ? 'Causa' : nodeType === 'EFFECT' ? 'Efeito' : 'Operador';
    const typeBg = nodeType === 'CAUSE' ? 'bg-green-600' : nodeType === 'EFFECT' ? 'bg-blue-600' : 'bg-amber-600';
    const nodeRestrictions = restrictions.filter((r) => r.nodeCodes.includes(code));

    return (
      <div className="w-72 bg-surface border-l border-edge p-4 shrink-0 overflow-y-auto">
        <h3 className="text-sm text-gray-300 mb-4">Propriedades do No</h3>

        <div className="space-y-4">
          <Field label="ID">
            <input
              value={code}
              disabled
              className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-400"
            />
          </Field>

          <Field label="Tipo">
            <span className={`inline-block px-2 py-0.5 rounded text-xs text-white ${typeBg}`}>
              {typeLabel}
            </span>
          </Field>

          <Field label="Rotulo">
            <input
              value={label}
              onChange={(e) => updateNodeData(selectedNode.id, { label: e.target.value })}
              className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-200 focus:border-primary outline-none"
              placeholder="Digite o rotulo"
            />
          </Field>

          {nodeType === 'OPERATOR' && (
            <Field label="Tipo de Operador">
              <select
                value={operatorType ?? ''}
                onChange={(e) => updateNodeData(selectedNode.id, { operatorType: e.target.value as OperatorType })}
                className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-200 focus:border-primary outline-none"
              >
                <option value="AND">AND</option>
                <option value="OR">OR</option>
              </select>
            </Field>
          )}

          <Field label="Posicao">
            <div className="flex gap-2">
              <input
                type="number"
                value={Math.round(selectedNode.position.x)}
                disabled
                className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-400"
                placeholder="X"
              />
              <input
                type="number"
                value={Math.round(selectedNode.position.y)}
                disabled
                className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-400"
                placeholder="Y"
              />
            </div>
          </Field>

          <Field label="Restricoes">
            {nodeRestrictions.length === 0 ? (
              <p className="text-xs text-gray-600 py-1">Nenhuma restricao aplicada</p>
            ) : (
              <div className="space-y-1.5">
                {nodeRestrictions.map((r, i) => {
                  const others = r.nodeCodes.filter((c) => c !== code);
                  return (
                    <div key={i} className="flex items-center gap-2 bg-surface-card border border-edge rounded px-2 py-1.5">
                      <span className="w-6 h-6 rounded flex items-center justify-center text-xs font-bold bg-surface-hover text-primary-light border border-edge shrink-0">
                        {RESTRICTION_SYMBOL[r.type]}
                      </span>
                      <div className="min-w-0">
                        <span className="text-xs text-gray-300 block">{RESTRICTION_LABEL[r.type]}</span>
                        {others.length > 0 && (
                          <span className="text-xs text-gray-500 truncate block">com {others.join(', ')}</span>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </Field>
        </div>
      </div>
    );
  }

  if (selectedEdge) {
    const isNegation = selectedEdge.type === 'negation';

    return (
      <div className="w-72 bg-surface border-l border-edge p-4 shrink-0 overflow-y-auto">
        <h3 className="text-sm text-gray-300 mb-4">Propriedades da Aresta</h3>

        <div className="space-y-4">
          <Field label="ID">
            <input
              value={selectedEdge.id}
              disabled
              className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-400"
            />
          </Field>

          <Field label="Tipo">
            <select
              value={isNegation ? 'negation' : 'default'}
              onChange={(e) => updateEdgeType(selectedEdge.id, e.target.value as 'default' | 'negation')}
              className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-200 focus:border-primary outline-none"
            >
              <option value="default">Identidade</option>
              <option value="negation">Negacao</option>
            </select>
          </Field>

          <Field label="Origem">
            <input
              value={selectedEdge.source}
              disabled
              className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-400"
            />
          </Field>

          <Field label="Destino">
            <input
              value={selectedEdge.target}
              disabled
              className="w-full bg-surface-card border border-edge rounded px-3 py-1.5 text-sm text-gray-400"
            />
          </Field>
        </div>
      </div>
    );
  }

  return null;
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <label className="text-xs text-gray-500 mb-1.5 block">{label}</label>
      {children}
    </div>
  );
}
