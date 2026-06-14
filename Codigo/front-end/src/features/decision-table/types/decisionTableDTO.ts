export type DecisionTableSyncStatus = 'UP_TO_DATE' | 'STALE';
export type BackendConditionValue = 'YES' | 'NO' | 'IRRELEVANT';
export type BackendActionValue = 'YES' | 'NO';

export interface DecisionTableConditionDTO {
  id: string;
  decisionTableId: string;
  code: string;
  label: string;
  orderIndex: number;
  createdAt: string;
  updatedAt: string;
}

export interface DecisionTableActionDTO {
  id: string;
  decisionTableId: string;
  code: string;
  label: string;
  orderIndex: number;
  createdAt: string;
  updatedAt: string;
}

export interface DecisionTableRuleDTO {
  id: string;
  decisionTableId: string;
  code: string;
  description: string;
  orderIndex: number;
  createdAt: string;
  updatedAt: string;
}

export interface DecisionTableConditionCellDTO {
  id: string;
  ruleId: string;
  conditionId: string;
  value: BackendConditionValue;
  createdAt: string;
  updatedAt: string;
}

export interface DecisionTableActionCellDTO {
  id: string;
  ruleId: string;
  actionId: string;
  value: BackendActionValue;
  createdAt: string;
  updatedAt: string;
}

export interface DecisionTableDTO {
  id: string;
  gceId: string;
  projectId: string;
  name: string;
  description: string;
  sourceFingerprint: string;
  syncStatus: DecisionTableSyncStatus;
  sourceGceUpdatedAt: string;
  createdAt: string;
  updatedAt: string;
  conditions: DecisionTableConditionDTO[];
  actions: DecisionTableActionDTO[];
  rules: DecisionTableRuleDTO[];
  conditionCells: DecisionTableConditionCellDTO[];
  actionCells: DecisionTableActionCellDTO[];
}

export interface UpdateDecisionTableDetailsDTO {
  name: string;
  description?: string;
}

// Assinatura de teste funcional (gerada a partir da tabela de decisão).
export interface FunctionalTestConditionDTO {
  conditionId: string;
  code: string;
  label: string;
  value: BackendConditionValue;
}

export interface FunctionalTestActionDTO {
  actionId: string;
  code: string;
  label: string;
  value: BackendActionValue;
}

export interface FunctionalTestMethodSignatureDTO {
  ruleId: string;
  ruleCode: string;
  methodName: string;
  conditions: FunctionalTestConditionDTO[];
  actions: FunctionalTestActionDTO[];
  generatedCode: string;
}

export interface GenerateFunctionalTestSignatureResponseDTO {
  decisionTableId: string;
  gceId: string;
  projectId: string;
  decisionTableName: string;
  rulesCount: number;
  testMethods: FunctionalTestMethodSignatureDTO[];
  generatedCode: string;
  warnings: string[];
}
