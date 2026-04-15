import type { Node, Edge } from '@xyflow/react';

export type GCENodeType = 'CAUSE' | 'EFFECT' | 'OPERATOR';
export type OperatorType = 'AND' | 'OR';
export type GCEEdgeType = 'IDENTITY' | 'NEGATED';
export type RestrictionType = 'EXCLUSIVE' | 'INCLUSIVE' | 'ONE_AND_ONLY_ONE' | 'REQUIRE' | 'MASKS';

export interface GCENodeData extends Record<string, unknown> {
  code: string;
  label: string;
  nodeType: GCENodeType;
  operatorType: OperatorType | null;
  hasError?: boolean;
}

export interface GCEEdgeData extends Record<string, unknown> {
  edgeType: GCEEdgeType;
  backendId?: string;
  bend?: { x: number; y: number };
}

export type GCEFlowNode = Node<GCENodeData, 'cause' | 'effect' | 'operator'>;
export type GCEFlowEdge = Edge<GCEEdgeData>;

export interface GCERestriction {
  id?: string;
  type: RestrictionType;
  nodeCodes: string[];
}

export interface GCEValidationError {
  id: string;
  elementId: string;
  elementType: 'node' | 'edge';
  message: string;
}

// ──────────────────────────────────────────────
// Backend DTOs (shapes returned/sent to the API)
// ──────────────────────────────────────────────

export interface GCENodeDTO {
  id?: string;
  code: string;
  label?: string;
  type: GCENodeType;
  operatorType: OperatorType | null;
  /** Positions are stored client-side only (not in the backend).  */
  position?: { x: number; y: number };
  sourceNodeCodes?: string[];
  targetNodeCodes?: string[];
}

export interface GCEEdgeDTO {
  id?: string;
  sourceNodeCode: string;
  targetNodeCode: string;
  type: GCEEdgeType;
}

export interface GCERestrictionDTO {
  id?: string;
  type: RestrictionType;
  nodeCodes: string[];
}

export interface GCEDTO {
  id: string;
  projectId: string;
  name: string;
  description: string;
  selected: boolean;
  createdAt: string;
  updatedAt: string | null;
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

// ──────────────────────────────────────────────
// Backend validation response
// ──────────────────────────────────────────────

export interface GCEValidationMessage {
  code: string;
  message: string;
}

export interface GCEValidationResponse {
  valid: boolean;
  errors: GCEValidationMessage[];
  warnings: GCEValidationMessage[];
}
