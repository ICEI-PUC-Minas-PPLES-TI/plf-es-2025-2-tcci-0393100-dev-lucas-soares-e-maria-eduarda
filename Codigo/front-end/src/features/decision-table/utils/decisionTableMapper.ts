import type { DecisionTableDTO } from '../types/decisionTableDTO';
import type {
  DecisionTable,
  DecisionCondition,
  DecisionEffect,
  DecisionRule,
  ConditionValue,
  EffectValue,
} from '../types/decisionTable';

export function mapDTOToDecisionTable(dto: DecisionTableDTO): DecisionTable {
  const conditions: DecisionCondition[] = [...dto.conditions]
    .sort((a, b) => a.orderIndex - b.orderIndex)
    .map((c) => ({
      id: c.id,
      order: c.orderIndex,
      label: c.label,
      gceNodeCode: c.code,
    }));

  const effects: DecisionEffect[] = [...dto.actions]
    .sort((a, b) => a.orderIndex - b.orderIndex)
    .map((a) => ({
      id: a.id,
      order: a.orderIndex,
      label: a.label,
      gceNodeCode: a.code,
    }));

  const rules: DecisionRule[] = [...dto.rules]
    .sort((a, b) => a.orderIndex - b.orderIndex)
    .map((rule) => {
      const conditionsMap: Record<string, ConditionValue> = {};
      for (const cell of dto.conditionCells) {
        if (cell.ruleId === rule.id) {
          conditionsMap[cell.conditionId] =
            cell.value === 'YES' ? 'S' : cell.value === 'NO' ? 'N' : '—';
        }
      }

      const effectsMap: Record<string, EffectValue> = {};
      for (const cell of dto.actionCells) {
        if (cell.ruleId === rule.id) {
          effectsMap[cell.actionId] = cell.value === 'YES' ? 'S' : 'N';
        }
      }

      return {
        id: rule.id,
        order: rule.orderIndex,
        conditions: conditionsMap,
        effects: effectsMap,
      };
    });

  return {
    id: dto.id,
    gceId: dto.gceId,
    projectId: dto.projectId,
    name: dto.name,
    description: dto.description,
    syncStatus: dto.syncStatus,
    conditions,
    effects,
    rules,
    generatedAt: dto.createdAt,
    updatedAt: dto.updatedAt,
  };
}
