import type { Node, Edge } from '@xyflow/react';

// ──────────────────────────────────────────────
// Backend enums (espelho de GfcNodeTypeEnum/GfcEdgeTypeEnum)
// ──────────────────────────────────────────────

export type GFCNodeType =
  | 'START'
  | 'END'
  | 'STATEMENT'
  | 'DECISION'
  | 'LOOP'
  | 'RETURN'
  | 'BREAK'
  | 'CONTINUE'
  | 'THROW'
  | 'SWITCH'
  | 'CASE'
  | 'CASE_BLOCK'
  | 'TRY'
  | 'CATCH'
  | 'FINALLY'
  | 'TERNARY';

export type GFCEdgeType =
  | 'SEQUENTIAL'
  | 'TRUE_BRANCH'
  | 'FALSE_BRANCH'
  | 'LOOP_BACK'
  | 'LOOP_BODY'
  | 'LOOP_EXIT'
  | 'CASE_BRANCH'
  | 'DEFAULT_BRANCH'
  | 'TRY_BRANCH'
  | 'CATCH_BRANCH'
  | 'FINALLY_BRANCH'
  | 'BREAK_FLOW'
  | 'CONTINUE_FLOW'
  | 'THROW_FLOW';

// ──────────────────────────────────────────────
// Backend DTOs
// ──────────────────────────────────────────────

export interface GFCNodeDTO {
  id: string;
  code: string;
  label: string;
  type: GFCNodeType;
  startLine: number | null;
  endLine: number | null;
}

export interface GFCEdgeDTO {
  id: string;
  sourceNodeCode: string;
  targetNodeCode: string;
  type: GFCEdgeType;
  label: string | null;
}

export interface GFCDTO {
  id: string;
  projectId: string;
  sourceFileId: string;
  methodSignature: string;
  name: string;
  description: string | null;
  language: string | null;
  nodes: GFCNodeDTO[];
  edges: GFCEdgeDTO[];
}

export interface GFCSummaryDTO {
  id: string;
  projectId: string;
  sourceFileId: string;
  methodSignature: string;
  name: string;
  description: string | null;
  language: string | null;
}

export interface CreateGFCRequest {
  projectId: string;
  sourceFileId: string;
  methodSignature: string;
  name: string;
  description?: string;
}

export interface CreateGFCResponse {
  id_gfc: string;
  mensagem: string;
  status: number;
}

// ──────────────────────────────────────────────
// Source File DTOs
// ──────────────────────────────────────────────

export interface GFCSourceFileDTO {
  id: string;
  projectId: string;
  fileName: string;
  language: string;
  createdAt: string;
  updatedAt: string | null;
}

export interface GFCSourceMethodDTO {
  name: string;
  signature: string;
  startLine: number;
  endLine: number;
}

export interface GFCSourceCodeDTO {
  sourceCode: string;
}

export interface GFCSourceMethodCodeDTO {
  name: string;
  signature: string;
  startLine: number;
  endLine: number;
  sourceCode: string;
}

export interface CreateGFCSourceFileResponse {
  id_arquivo: string;
  mensagem: string;
  status: number;
}

// ──────────────────────────────────────────────
// React Flow types
// ──────────────────────────────────────────────

export interface GFCFlowNodeData extends Record<string, unknown> {
  code: string;
  label: string;
  nodeType: GFCNodeType;
  startLine: number | null;
  endLine: number | null;
}

export interface GFCFlowEdgeData extends Record<string, unknown> {
  edgeType: GFCEdgeType;
  backendId: string;
  label: string | null;
}

export type GFCFlowNodeKind =
  | 'start'
  | 'end'
  | 'statement'
  | 'decision'
  | 'loop'
  | 'return'
  | 'break'
  | 'continue'
  | 'throw'
  | 'switch'
  | 'case'
  | 'caseBlock'
  | 'try'
  | 'catch'
  | 'finally'
  | 'ternary';

export type GFCFlowNode = Node<GFCFlowNodeData, GFCFlowNodeKind>;
export type GFCFlowEdge = Edge<GFCFlowEdgeData>;
