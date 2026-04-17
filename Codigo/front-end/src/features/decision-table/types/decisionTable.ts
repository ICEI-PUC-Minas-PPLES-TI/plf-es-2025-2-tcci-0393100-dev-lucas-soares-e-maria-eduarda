export type ConditionValue = 'S' | 'N' | '—';
export type EffectValue = 'S' | 'N';

export interface DecisionCondition {
  /** Stable ID used as key in rule maps (e.g. "cond-0") */
  id: string;
  order: number;
  label: string;
  /** Code of the corresponding CAUSE node in the GCE */
  gceNodeCode: string;
}

export interface DecisionEffect {
  /** Stable ID used as key in rule maps (e.g. "eff-0") */
  id: string;
  order: number;
  label: string;
  /** Code of the corresponding EFFECT node in the GCE */
  gceNodeCode: string;
}

export interface DecisionRule {
  id: string;
  order: number;
  /** conditionId → 'S' | 'N' | '—' */
  conditions: Record<string, ConditionValue>;
  /** effectId → 'S' | 'N' */
  effects: Record<string, EffectValue>;
}

export interface DecisionTable {
  id: string;
  gceId: string;
  projectId: string;
  name: string;
  description?: string;
  syncStatus?: 'UP_TO_DATE' | 'STALE';
  conditions: DecisionCondition[];
  effects: DecisionEffect[];
  rules: DecisionRule[];
  generatedAt: string;
  updatedAt: string;
}
