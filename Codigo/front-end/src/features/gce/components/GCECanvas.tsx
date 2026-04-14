import { useCallback, useMemo, useState, forwardRef, useImperativeHandle, useEffect, useRef } from 'react';
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  addEdge,
  reconnectEdge,
  useNodesState,
  useEdgesState,
  useReactFlow,
  ConnectionMode,
  ConnectionLineType,
  type OnConnect,
  type OnReconnect,
  type OnSelectionChangeFunc,
  BackgroundVariant,
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';

import { CauseNode } from './CauseNode';
import { EffectNode } from './EffectNode';
import { OperatorNode } from './OperatorNode';
import { NegationEdge } from './NegationEdge';
import { EditableEdge } from './EditableEdge';
import { ConstraintMenu } from './ConstraintMenu';
import type { GCEFlowNode, GCEFlowEdge, GCENodeType, OperatorType, RestrictionType, GCERestriction } from '../types/gce';

interface GCECanvasProps {
  initialNodes: GCEFlowNode[];
  initialEdges: GCEFlowEdge[];
  initialRestrictions: GCERestriction[];
  onSelectionChange: (nodeId: string | null, edgeId: string | null) => void;
  onRestrictionsChange?: (restrictions: GCERestriction[]) => void;
  onChange?: () => void;
}

export interface GCECanvasHandle {
  addNode: (type: GCENodeType, operatorType?: OperatorType) => void;
  deleteSelected: () => void;
  hasSelection: boolean;
  getState: () => { nodes: GCEFlowNode[]; edges: GCEFlowEdge[]; restrictions: GCERestriction[] };
}

const nodeTypes = { cause: CauseNode, effect: EffectNode, operator: OperatorNode };
const edgeTypes = { negation: NegationEdge, editable: EditableEdge };

let nodeCounter = 100;

export const GCECanvas = forwardRef<GCECanvasHandle, GCECanvasProps>(
  function GCECanvas({ initialNodes, initialEdges, initialRestrictions, onSelectionChange, onRestrictionsChange, onChange }, ref) {
    const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
    const [restrictions, setRestrictions] = useState<GCERestriction[]>(initialRestrictions);
    const [multiSelected, setMultiSelected] = useState<string[]>([]);
    const [constraintMenuPos, setConstraintMenuPos] = useState<{ x: number; y: number } | null>(null);
    const { flowToScreenPosition } = useReactFlow();

    const onReconnect: OnReconnect = useCallback(
      (oldEdge, newConnection) => {
        setEdges((eds) => {
          const duplicate = eds.some(
            (e) => e.id !== oldEdge.id && e.source === newConnection.source && e.target === newConnection.target,
          );
          if (duplicate) return eds;
          return reconnectEdge(oldEdge, newConnection, eds) as GCEFlowEdge[];
        });
      },
      [setEdges],
    );

    const onConnect: OnConnect = useCallback(
      (params) => {
        setEdges((eds) => {
          const duplicate = eds.some(
            (e) => e.source === params.source && e.target === params.target,
          );
          if (duplicate) return eds;
          return addEdge({ ...params, type: 'editable', data: { edgeType: 'IDENTITY' } }, eds);
        });
      },
      [setEdges],
    );

    const handleSelectionChange: OnSelectionChangeFunc = useCallback(
      ({ nodes: selectedNodes, edges: selectedEdges }) => {
        const nodeIds = selectedNodes.map((n) => n.id);
        setMultiSelected(nodeIds);

        if (nodeIds.length >= 2) {
          const maxX = Math.max(...selectedNodes.map((n) => (n.position?.x ?? 0) + (n.measured?.width ?? 140)));
          const minY = Math.min(...selectedNodes.map((n) => n.position?.y ?? 0));
          const maxY = Math.max(...selectedNodes.map((n) => (n.position?.y ?? 0) + (n.measured?.height ?? 60)));
          const centerY = (minY + maxY) / 2;
          const screenPos = flowToScreenPosition({ x: maxX + 20, y: centerY });
          setConstraintMenuPos({ x: screenPos.x, y: screenPos.y });
        } else {
          setConstraintMenuPos(null);
        }

        if (selectedNodes.length === 1) {
          onSelectionChange(selectedNodes[0].id, null);
        } else if (selectedEdges.length === 1) {
          onSelectionChange(null, selectedEdges[0].id);
        } else if (selectedNodes.length === 0 && selectedEdges.length === 0) {
          onSelectionChange(null, null);
        }
      },
      [onSelectionChange, flowToScreenPosition],
    );

    const addNode = useCallback(
      (type: GCENodeType, operatorType?: OperatorType) => {
        nodeCounter++;
        const prefix = type === 'CAUSE' ? 'C' : type === 'EFFECT' ? 'E' : 'O';
        const code = `${prefix}${nodeCounter}`;
        const flowType = type === 'CAUSE' ? 'cause' : type === 'EFFECT' ? 'effect' : 'operator';
        const labels = { CAUSE: 'Nova causa', EFFECT: 'Novo efeito', OPERATOR: operatorType ?? 'OP' };

        const newNode: GCEFlowNode = {
          id: code,
          type: flowType,
          position: { x: 200 + Math.random() * 200, y: 100 + Math.random() * 200 },
          data: {
            code,
            label: labels[type] as string,
            nodeType: type,
            operatorType: operatorType ?? null,
          },
        };

        setNodes((nds) => [...nds, newNode]);
      },
      [setNodes],
    );

    const deleteSelected = useCallback(() => {
      const deletedNodeIds = new Set(nodes.filter((n) => n.selected).map((n) => n.id));
      setNodes((nds) => nds.filter((n) => !n.selected));
      setEdges((eds) =>
        eds.filter(
          (e) => !e.selected && !deletedNodeIds.has(e.source) && !deletedNodeIds.has(e.target),
        ),
      );
      onSelectionChange(null, null);
    }, [nodes, setNodes, setEdges, onSelectionChange]);

    const handleConstraint = useCallback(
      (type: RestrictionType) => {
        setRestrictions((prev) => [...prev, { type, nodeCodes: [...multiSelected] }]);
        setConstraintMenuPos(null);
        setMultiSelected([]);
      },
      [multiSelected],
    );

    const hasSelection = useMemo(() => {
      return nodes.some((n) => n.selected) || edges.some((e) => e.selected);
    }, [nodes, edges]);

    const getState = useCallback(
      () => ({ nodes, edges, restrictions }),
      [nodes, edges, restrictions],
    );

    useImperativeHandle(ref, () => ({ addNode, deleteSelected, hasSelection, getState }), [addNode, deleteSelected, hasSelection, getState]);

    useEffect(() => {
      onRestrictionsChange?.(restrictions);
    }, [restrictions, onRestrictionsChange]);

    const nodeStructure = useMemo(
      () => nodes.map((n) => `${n.id}:${n.type}:${n.data.label}:${n.data.operatorType}`).sort().join('|'),
      [nodes],
    );
    const edgeStructure = useMemo(
      () => edges.map((e) => `${e.source}->${e.target}:${e.type}`).sort().join('|'),
      [edges],
    );

    const isFirstRender = useRef(true);
    useEffect(() => {
      if (isFirstRender.current) { isFirstRender.current = false; return; }
      onChange?.();
    }, [nodeStructure, edgeStructure, restrictions, onChange]);

    return (
      <div className="flex-1 relative">
        {multiSelected.length === 0 && (
          <div className="absolute top-4 left-1/2 -translate-x-1/2 z-10 pointer-events-none">
            <div className="bg-surface-card border border-edge rounded-lg px-4 py-2 shadow-lg">
              <p className="text-xs text-gray-500 flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-primary-light animate-pulse" />
                Segure <kbd className="px-1.5 py-0.5 bg-surface-hover border border-edge rounded text-gray-200 text-xs">Shift</kbd> ou <kbd className="px-1.5 py-0.5 bg-surface-hover border border-edge rounded text-gray-200 text-xs">Ctrl</kbd> para selecionar multiplos nós
              </p>
            </div>
          </div>
        )}

        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onConnect={onConnect}
          onReconnect={onReconnect}
          onSelectionChange={handleSelectionChange}
          nodeTypes={nodeTypes}
          edgeTypes={edgeTypes}
          fitView
          fitViewOptions={{ padding: 0.2 }}
          selectionOnDrag
          panOnScroll
          multiSelectionKeyCode="Shift"
          connectionMode={ConnectionMode.Loose}
          connectionLineType={ConnectionLineType.Bezier}
          connectionLineStyle={{ stroke: '#8b949e', strokeWidth: 2, strokeDasharray: '6 3' }}
          selectNodesOnDrag={false}
          elevateEdgesOnSelect
          onlyRenderVisibleElements
          defaultEdgeOptions={{
            style: { stroke: 'var(--color-edge-hover)', strokeWidth: 2 },
            type: 'editable',
          }}
          proOptions={{ hideAttribution: true }}
          className="bg-surface!"
        >
          <Background variant={BackgroundVariant.Dots} gap={32} size={0.8} color="var(--color-edge)" />
          <Controls
            className="bg-surface-card! border-edge! shadow-lg! [&>button]:bg-surface-card! [&>button]:border-edge! [&>button]:text-gray-300! [&>button:hover]:bg-surface-hover!"
          />
          <MiniMap
            nodeColor={(n) => {
              if (n.type === 'cause') return 'var(--color-node-cause)';
              if (n.type === 'effect') return 'var(--color-node-effect)';
              return 'var(--color-node-operator)';
            }}
            className="bg-surface-card! border-edge!"
            maskColor="rgba(0, 0, 0, 0.5)"
          />
        </ReactFlow>

        {constraintMenuPos && multiSelected.length >= 2 && (
          <ConstraintMenu
            position={constraintMenuPos}
            selectedCount={multiSelected.length}
            selectedNodeTypes={multiSelected.map((id) => nodes.find((n) => n.id === id)?.type ?? '')}
            onSelectConstraint={handleConstraint}
            onClose={() => setConstraintMenuPos(null)}
          />
        )}
      </div>
    );
  },
);
