import { useEffect, useMemo } from 'react';
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  useNodesState,
  useEdgesState,
  BackgroundVariant,
  type EdgeTypes,
  type OnSelectionChangeFunc,
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';

import { StartNode } from './nodes/StartNode';
import { EndNode } from './nodes/EndNode';
import { StatementNode } from './nodes/StatementNode';
import { DecisionNode } from './nodes/DecisionNode';
import { ReturnNode } from './nodes/ReturnNode';
import { GFCLegend } from './GFCLegend';
import { GFCStats } from './GFCStats';
import { savePositions, type GFCStats as Stats } from '../utils/gfcConverters';
import type { GFCFlowNode, GFCFlowEdge } from '../types/gfc';

interface GFCCanvasProps {
  gfcId: string;
  initialNodes: GFCFlowNode[];
  initialEdges: GFCFlowEdge[];
  stats: Stats;
  onNodeSelect: (nodeId: string | null) => void;
}

const nodeTypes = {
  start: StartNode,
  end: EndNode,
  statement: StatementNode,
  decision: DecisionNode,
  return: ReturnNode,
};

const edgeTypes: EdgeTypes = {};

function edgeStyle(type: string | undefined): { stroke: string; strokeWidth: number } {
  if (type === 'TRUE_BRANCH') return { stroke: 'var(--color-gfc-edge-true)', strokeWidth: 2 };
  if (type === 'FALSE_BRANCH') return { stroke: 'var(--color-gfc-edge-false)', strokeWidth: 2 };
  if (type === 'LOOP_BACK') return { stroke: 'var(--color-gfc-edge-loop)', strokeWidth: 2 };
  return { stroke: 'var(--color-edge-hover)', strokeWidth: 2 };
}

export function GFCCanvas({ gfcId, initialNodes, initialEdges, stats, onNodeSelect }: GFCCanvasProps) {
  const styledEdges = useMemo(
    () =>
      initialEdges.map((e) => ({
        ...e,
        style: edgeStyle(e.data?.edgeType),
        labelStyle: { fill: 'var(--color-edge-hover)', fontSize: 11, fontFamily: 'monospace' },
        labelBgStyle: { fill: 'var(--color-surface-card)' },
        labelBgPadding: [4, 2] as [number, number],
        labelBgBorderRadius: 4,
        markerEnd: { type: 'arrowclosed' as const, color: edgeStyle(e.data?.edgeType).stroke },
      })),
    [initialEdges],
  );

  const [nodes, , onNodesChange] = useNodesState<GFCFlowNode>(initialNodes);
  const [edges, , onEdgesChange] = useEdgesState<GFCFlowEdge>(styledEdges);

  // Persiste posições no localStorage sempre que mudam (debounce simples via efeito)
  useEffect(() => {
    const handle = setTimeout(() => savePositions(gfcId, nodes), 400);
    return () => clearTimeout(handle);
  }, [gfcId, nodes]);

  const handleSelection: OnSelectionChangeFunc = ({ nodes: selectedNodes }) => {
    onNodeSelect(selectedNodes.length === 1 ? selectedNodes[0].id : null);
  };

  return (
    <div className="flex-1 relative">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onSelectionChange={handleSelection}
        nodeTypes={nodeTypes}
        edgeTypes={edgeTypes}
        fitView
        fitViewOptions={{ padding: 0.25, maxZoom: 0.9, minZoom: 0.25 }}
        nodesConnectable={false}
        edgesFocusable={false}
        edgesReconnectable={false}
        proOptions={{ hideAttribution: true }}
        className="bg-surface!"
      >
        <Background variant={BackgroundVariant.Dots} gap={28} size={0.8} color="var(--color-edge)" />
        <Controls className="bg-surface-card! border-edge! shadow-lg! [&>button]:bg-surface-card! [&>button]:border-edge! [&>button]:text-gray-300! [&>button:hover]:bg-surface-hover!" />
        <MiniMap
          nodeColor={(n) => {
            if (n.type === 'start') return 'var(--color-gfc-start)';
            if (n.type === 'end') return 'var(--color-gfc-end)';
            if (n.type === 'decision') return 'var(--color-gfc-decision)';
            if (n.type === 'return') return 'var(--color-gfc-return)';
            return 'var(--color-gfc-statement)';
          }}
          className="bg-surface-card! border-edge!"
          maskColor="rgba(0, 0, 0, 0.45)"
        />
      </ReactFlow>

      <div className="absolute top-4 left-4 flex flex-col gap-3 pointer-events-auto">
        <GFCLegend />
        <GFCStats stats={stats} />
      </div>
    </div>
  );
}
