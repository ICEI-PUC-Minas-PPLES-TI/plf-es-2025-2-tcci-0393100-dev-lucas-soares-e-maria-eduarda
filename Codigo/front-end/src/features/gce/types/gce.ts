import type { Node, Edge } from '@xyflow/react';

export type GCENodeType = 'CAUSE' | 'EFFECT' | 'OPERATOR';
export type OperatorType = 'AND' | 'OR' | 'NOT';
export type GCEEdgeType = 'IDENTITY' | 'NEGATION';
export type RestrictionType = 'EXCLUSIVE' | 'INCLUSIVE' | 'ONLY_ONE' | 'REQUIRES' | 'MASK';

export interface GCENodeData extends Record<string, unknown> {
  code: string;
  label: string;
  nodeType: GCENodeType;
  operatorType: OperatorType | null;
  hasError?: boolean;
}

export interface GCEEdgeData extends Record<string, unknown> {
  edgeType: GCEEdgeType;
  label?: string;
}

export type GCEFlowNode = Node<GCENodeData, 'cause' | 'effect' | 'operator'>;
export type GCEFlowEdge = Edge<GCEEdgeData>;

export interface GCERestriction {
  type: RestrictionType;
  nodeCodes: string[];
}

export interface GCEValidationError {
  id: string;
  elementId: string;
  elementType: 'node' | 'edge';
  message: string;
}

// Backend DTOs

export interface GCENodeDTO {
  code: string;
  label: string;
  type: GCENodeType;
  operatorType: OperatorType | null;
}

export interface GCEEdgeDTO {
  sourceNodeCode: string;
  targetNodeCode: string;
  type: GCEEdgeType;
}

export interface GCERestrictionDTO {
  type: RestrictionType;
  nodeCodes: string[];
}

export interface GCEDTO {
  id: string;
  projectId: string;
  name: string;
  description: string;
  selected: boolean;
  nodes: GCENodeDTO[];
  edges: GCEEdgeDTO[];
  restrictions: GCERestrictionDTO[];
}

export interface CreateGCERequest {
  projectId: string;
  name: string;
  description: string;
  selected: boolean;
  nodes: GCENodeDTO[];
  edges: GCEEdgeDTO[];
  restrictions: GCERestrictionDTO[];
}
