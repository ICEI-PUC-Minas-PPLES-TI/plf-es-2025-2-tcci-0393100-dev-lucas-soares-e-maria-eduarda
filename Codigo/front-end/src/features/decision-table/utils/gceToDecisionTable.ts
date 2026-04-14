import type { GCEDTO, GCEEdgeType, GCENodeDTO, GCERestrictionDTO } from '../../gce/types/gce';
import type {
  ConditionValue,
  DecisionCondition,
  DecisionEffect,
  DecisionRule,
  DecisionTable,
  EffectValue,
} from '../types/decisionTable';

/** Max number of CAUSE nodes before generation is skipped (2^12 = 4096 combinations max) */
const MAX_CAUSES = 12;

// ─── Graph evaluation ────────────────────────────────────────────────────────

function buildIncomingMap(
  edges: { sourceNodeCode: string; targetNodeCode: string; type: GCEEdgeType }[],
): Map<string, Array<{ source: string; edgeType: GCEEdgeType }>> {
  const map = new Map<string, Array<{ source: string; edgeType: GCEEdgeType }>>();
  for (const edge of edges) {
    if (!map.has(edge.targetNodeCode)) map.set(edge.targetNodeCode, []);
    map.get(edge.targetNodeCode)!.push({ source: edge.sourceNodeCode, edgeType: edge.type });
  }
  return map;
}

/**
 * Propagates cause values through the GCE graph using iterative fixed-point
 * evaluation. Returns a map of ALL node codes → computed boolean values.
 */
function evaluateGraph(
  nodes: GCENodeDTO[],
  edges: { sourceNodeCode: string; targetNodeCode: string; type: GCEEdgeType }[],
  causeValues: Map<string, boolean>,
): Map<string, boolean> {
  const incoming = buildIncomingMap(edges);
  const values = new Map<string, boolean>(causeValues);

  let changed = true;
  while (changed) {
    changed = false;

    for (const node of nodes) {
      if (node.type === 'CAUSE') continue;
      if (values.has(node.code)) continue;

      const inEdges = incoming.get(node.code) ?? [];
      if (inEdges.length === 0) continue;

      let allReady = true;
      const inputVals: boolean[] = [];

      for (const { source, edgeType } of inEdges) {
        if (!values.has(source)) { allReady = false; break; }
        const raw = values.get(source)!;
        inputVals.push(edgeType === 'NEGATED' ? !raw : raw);
      }

      if (!allReady) continue;

      let result: boolean;
      if (node.type === 'OPERATOR') {
        result = (node.operatorType ?? 'OR') === 'AND'
          ? inputVals.every(Boolean)
          : inputVals.some(Boolean);
      } else {
        // EFFECT: active if any incoming signal is true
        result = inputVals.some(Boolean);
      }

      values.set(node.code, result);
      changed = true;
    }
  }

  return values;
}

// ─── Restriction validation ───────────────────────────────────────────────────

/**
 * Returns false if the given cause combination violates any cause-side
 * restriction (EXCLUSIVE, INCLUSIVE, ONE_AND_ONLY_ONE, REQUIRE).
 * Restrictions referencing non-cause nodes are skipped.
 */
function isValidCombination(
  restrictions: GCERestrictionDTO[],
  causeValues: Map<string, boolean>,
): boolean {
  for (const r of restrictions) {
    const vals = r.nodeCodes.map(code => causeValues.get(code));

    // Skip restrictions that involve non-cause nodes (undefined means not a cause)
    if (vals.some(v => v === undefined)) continue;

    const bools = vals as boolean[];
    const trueCount = bools.filter(Boolean).length;

    switch (r.type) {
      case 'EXCLUSIVE':
        if (trueCount > 1) return false;
        break;
      case 'INCLUSIVE':
        if (trueCount === 0) return false;
        break;
      case 'ONE_AND_ONLY_ONE':
        if (trueCount !== 1) return false;
        break;
      case 'REQUIRE':
        // nodeCodes = [c1, c2]: if c1 is true then c2 must be true
        if (bools[0] === true && bools[1] === false) return false;
        break;
      default:
        break;
    }
  }
  return true;
}

/**
 * Applies MASKS restrictions to effect values in-place.
 * MASKS: if effect[code1] is true, force effect[code2] to false.
 */
function applyMasks(restrictions: GCERestrictionDTO[], values: Map<string, boolean>): void {
  for (const r of restrictions) {
    if (r.type !== 'MASKS') continue;
    const [code1, code2] = r.nodeCodes;
    if (values.get(code1) === true) values.set(code2, false);
  }
}

// ─── Public API ───────────────────────────────────────────────────────────────

/**
 * Generates a DecisionTable from a GCEDTO by evaluating all valid
 * combinations of CAUSE node values and propagating them through the graph.
 *
 * Returns an empty rules array when:
 * - There are no CAUSE nodes in the GCE, or
 * - There are more than MAX_CAUSES (12) CAUSE nodes (combinatorial explosion).
 */
export function convertGCEToDecisionTable(
  gce: GCEDTO,
): Omit<DecisionTable, 'id' | 'updatedAt'> {
  const causeNodes = gce.nodes.filter(n => n.type === 'CAUSE');
  const effectNodes = gce.nodes.filter(n => n.type === 'EFFECT');

  const conditions: DecisionCondition[] = causeNodes.map((node, idx) => ({
    id: `cond-${idx}`,
    order: idx + 1,
    label: node.label,
    gceNodeCode: node.code,
  }));

  const effects: DecisionEffect[] = effectNodes.map((node, idx) => ({
    id: `eff-${idx}`,
    order: idx + 1,
    label: node.label,
    gceNodeCode: node.code,
  }));

  const base = {
    gceId: gce.id,
    projectId: gce.projectId,
    name: `Tabela — ${gce.name}`,
    conditions,
    effects,
    generatedAt: new Date().toISOString(),
  };

  const n = causeNodes.length;
  if (n === 0 || n > MAX_CAUSES) {
    return { ...base, rules: [] };
  }

  const rules: DecisionRule[] = [];
  const restrictions = gce.restrictions ?? [];

  for (let combo = 0; combo < Math.pow(2, n); combo++) {
    // Build cause values for this combination (MSB = first cause)
    const causeValues = new Map<string, boolean>();
    for (let j = 0; j < n; j++) {
      causeValues.set(causeNodes[j].code, ((combo >> (n - 1 - j)) & 1) === 1);
    }

    if (!isValidCombination(restrictions, causeValues)) continue;

    const allValues = evaluateGraph(gce.nodes, gce.edges, causeValues);
    applyMasks(restrictions, allValues);

    const conditionsMap: Record<string, ConditionValue> = {};
    for (const cond of conditions) {
      conditionsMap[cond.id] = causeValues.get(cond.gceNodeCode) ? 'S' : 'N';
    }

    const effectsMap: Record<string, EffectValue> = {};
    for (const eff of effects) {
      effectsMap[eff.id] = allValues.get(eff.gceNodeCode) ? 'S' : 'N';
    }

    rules.push({
      id: `rule-${combo}`,
      order: rules.length + 1,
      conditions: conditionsMap,
      effects: effectsMap,
    });
  }

  return { ...base, rules };
}

/**
 * Creates a blank rule with all conditions set to '—' (don't care)
 * and all effects set to 'N'. Used when the user manually adds a rule.
 */
export function createEmptyRule(
  conditions: DecisionCondition[],
  effects: DecisionEffect[],
  order: number,
): DecisionRule {
  return {
    id: `rule-manual-${Date.now()}`,
    order,
    conditions: Object.fromEntries(conditions.map(c => [c.id, '—' as ConditionValue])),
    effects: Object.fromEntries(effects.map(e => [e.id, 'N' as EffectValue])),
  };
}
