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
import { LoopNode } from './nodes/LoopNode';
import { SwitchNode } from './nodes/SwitchNode';
import { TernaryNode } from './nodes/TernaryNode';
import { BlockNode } from './nodes/BlockNode';
import { TerminatorNode } from './nodes/TerminatorNode';
import { OrthogonalEdge } from './edges/OrthogonalEdge';
import { GFCLegend } from './GFCLegend';
import { GFCStats } from './GFCStats';
import { savePositions, type GFCStats as Stats } from '../utils/gfcConverters';
import type { GFCCyclomaticComplexityDTO, GFCFlowNode, GFCFlowEdge } from '../types/gfc';

interface GFCCanvasProps {
  gfcId: string;
  initialNodes: GFCFlowNode[];
  initialEdges: GFCFlowEdge[];
  stats: Stats;
  complexity?: GFCCyclomaticComplexityDTO | null;
  onNodeSelect: (nodeId: string | null) => void;
}

const nodeTypes = {
  start: StartNode,
  end: EndNode,
  statement: StatementNode,
  decision: DecisionNode,
  loop: LoopNode,
  return: ReturnNode,
  break: TerminatorNode,
  continue: TerminatorNode,
  throw: TerminatorNode,
  switch: SwitchNode,
  case: BlockNode,
  caseBlock: BlockNode,
  try: BlockNode,
  catch: BlockNode,
  finally: BlockNode,
  ternary: TernaryNode,
};

const edgeTypes: EdgeTypes = {
  orthogonal: OrthogonalEdge,
};

function edgeStyle(type: string | undefined): { stroke: string; strokeWidth: number; strokeDasharray?: string } {
  switch (type) {
    case 'TRUE_BRANCH': return { stroke: 'var(--color-gfc-edge-true)', strokeWidth: 2 };
    case 'FALSE_BRANCH': return { stroke: 'var(--color-gfc-edge-false)', strokeWidth: 2 };
    case 'LOOP_BACK': return { stroke: 'var(--color-gfc-edge-loop)', strokeWidth: 2, strokeDasharray: '6 4' };
    case 'LOOP_BODY': return { stroke: 'var(--color-gfc-edge-body)', strokeWidth: 2 };
    case 'LOOP_EXIT': return { stroke: 'var(--color-gfc-edge-exit)', strokeWidth: 2 };
    case 'CASE_BRANCH': return { stroke: 'var(--color-gfc-edge-case)', strokeWidth: 2 };
    case 'DEFAULT_BRANCH': return { stroke: 'var(--color-gfc-edge-default)', strokeWidth: 2, strokeDasharray: '4 3' };
    case 'TRY_BRANCH': return { stroke: 'var(--color-gfc-edge-try)', strokeWidth: 2 };
    case 'CATCH_BRANCH': return { stroke: 'var(--color-gfc-edge-catch)', strokeWidth: 2 };
    case 'FINALLY_BRANCH': return { stroke: 'var(--color-gfc-edge-finally)', strokeWidth: 2 };
    case 'BREAK_FLOW': return { stroke: 'var(--color-gfc-edge-break)', strokeWidth: 2, strokeDasharray: '5 3' };
    case 'CONTINUE_FLOW': return { stroke: 'var(--color-gfc-edge-continue)', strokeWidth: 2, strokeDasharray: '5 3' };
    case 'THROW_FLOW': return { stroke: 'var(--color-gfc-edge-throw)', strokeWidth: 2, strokeDasharray: '5 3' };
    default: return { stroke: 'var(--color-edge-hover)', strokeWidth: 2 };
  }
}

export function GFCCanvas({ gfcId, initialNodes, initialEdges, stats, complexity, onNodeSelect }: GFCCanvasProps) {
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
            switch (n.type) {
              case 'start': return 'var(--color-gfc-start)';
              case 'end': return 'var(--color-gfc-end)';
              case 'decision': return 'var(--color-gfc-decision)';
              case 'loop': return 'var(--color-gfc-loop)';
              case 'ternary': return 'var(--color-gfc-ternary)';
              case 'switch': return 'var(--color-gfc-switch)';
              case 'case': return 'var(--color-gfc-case)';
              case 'caseBlock': return 'var(--color-gfc-case-block)';
              case 'try': return 'var(--color-gfc-try)';
              case 'catch': return 'var(--color-gfc-catch)';
              case 'finally': return 'var(--color-gfc-finally)';
              case 'return': return 'var(--color-gfc-return)';
              case 'break': return 'var(--color-gfc-break)';
              case 'continue': return 'var(--color-gfc-continue)';
              case 'throw': return 'var(--color-gfc-throw)';
              default: return 'var(--color-gfc-statement)';
            }
          }}
          className="bg-surface-card! border-edge!"
          maskColor="rgba(0, 0, 0, 0.45)"
        />
      </ReactFlow>

      <div className="absolute top-4 left-4 flex flex-col gap-3 pointer-events-auto">
        <GFCLegend />
        <GFCStats stats={stats} complexity={complexity ?? null} />
      </div>
    </div>
  );
}
