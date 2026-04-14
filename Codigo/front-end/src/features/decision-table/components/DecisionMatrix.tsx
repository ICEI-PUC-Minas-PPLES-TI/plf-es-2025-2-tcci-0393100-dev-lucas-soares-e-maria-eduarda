import { Plus, Trash2 } from 'lucide-react';
import type {
  ConditionValue,
  DecisionCondition,
  DecisionEffect,
  DecisionRule,
  EffectValue,
} from '../types/decisionTable';

// ─── Cell toggle buttons ──────────────────────────────────────────────────────

interface ConditionToggleProps {
  value: ConditionValue;
  onChange: (v: ConditionValue) => void;
}

const CONDITION_OPTIONS: { v: ConditionValue; label: string; activeClass: string }[] = [
  { v: 'S', label: 'S', activeClass: 'bg-node-cause-bg text-white' },
  { v: 'N', label: 'N', activeClass: 'bg-node-error text-white' },
  { v: '—', label: '—', activeClass: 'bg-surface-elevated text-gray-400' },
];

function ConditionToggle({ value, onChange }: ConditionToggleProps) {
  return (
    <div className="flex gap-1 justify-center">
      {CONDITION_OPTIONS.map(opt => (
        <button
          key={opt.v}
          onClick={() => onChange(opt.v)}
          className={`w-7 h-7 rounded text-xs font-medium transition-colors ${
            value === opt.v
              ? opt.activeClass
              : 'bg-surface-hover text-gray-600 hover:text-gray-300 hover:bg-surface-elevated'
          }`}
        >
          {opt.label}
        </button>
      ))}
    </div>
  );
}

interface EffectToggleProps {
  value: EffectValue;
  onChange: (v: EffectValue) => void;
}

function EffectToggle({ value, onChange }: EffectToggleProps) {
  return (
    <div className="flex gap-1 justify-center">
      {(['S', 'N'] as EffectValue[]).map(v => (
        <button
          key={v}
          onClick={() => onChange(v)}
          className={`w-7 h-7 rounded text-xs font-medium transition-colors ${
            value === v
              ? v === 'S'
                ? 'bg-node-cause-bg text-white'
                : 'bg-node-error text-white'
              : 'bg-surface-hover text-gray-600 hover:text-gray-300 hover:bg-surface-elevated'
          }`}
        >
          {v}
        </button>
      ))}
    </div>
  );
}

// ─── Section header row ───────────────────────────────────────────────────────

function SectionRow({ label, span }: { label: string; span: number }) {
  return (
    <tr>
      <td
        colSpan={span}
        className="bg-surface-elevated border border-edge px-4 py-1 text-[10px] text-gray-500 uppercase tracking-widest"
      >
        {label}
      </td>
    </tr>
  );
}

// ─── Main component ───────────────────────────────────────────────────────────

interface DecisionMatrixProps {
  conditions: DecisionCondition[];
  effects: DecisionEffect[];
  rules: DecisionRule[];
  onConditionValueChange: (ruleId: string, conditionId: string, value: ConditionValue) => void;
  onEffectValueChange: (ruleId: string, effectId: string, value: EffectValue) => void;
  onRuleAdd: () => void;
  onRuleDelete: (ruleId: string) => void;
}

export function DecisionMatrix({
  conditions,
  effects,
  rules,
  onConditionValueChange,
  onEffectValueChange,
  onRuleAdd,
  onRuleDelete,
}: DecisionMatrixProps) {
  if (conditions.length === 0 && effects.length === 0) {
    return (
      <div className="flex-1 flex items-center justify-center text-center px-8">
        <div>
          <p className="text-sm text-gray-400 mb-1">Nenhuma condição ou efeito encontrado.</p>
          <p className="text-xs text-gray-600">
            Adicione nós de causa e efeito ao GCE e regenere a tabela.
          </p>
        </div>
      </div>
    );
  }

  const colSpan = rules.length + 2; // label col + rule cols + add col

  return (
    <div className="flex-1 overflow-auto p-4">
      <table className="border-collapse text-sm">
        {/* ── Header row ── */}
        <thead>
          <tr>
            <th className="sticky left-0 top-0 z-20 bg-surface-card border border-edge px-4 py-2 text-left min-w-[220px]">
              <span className="text-[10px] text-gray-500 uppercase tracking-wider">Elemento</span>
            </th>

            {rules.map(rule => (
              <th
                key={rule.id}
                className="sticky top-0 z-10 bg-surface-card border border-edge px-3 py-2 min-w-[130px] text-center"
              >
                <div className="flex items-center justify-between gap-1">
                  <span className="text-xs text-gray-400 font-normal">R{rule.order}</span>
                  <button
                    onClick={() => onRuleDelete(rule.id)}
                    className="text-gray-600 hover:text-node-error transition-colors"
                    title="Excluir regra"
                  >
                    <Trash2 className="w-3 h-3" />
                  </button>
                </div>
              </th>
            ))}

            {/* Add-rule column header */}
            <th className="sticky top-0 z-10 bg-surface-card border border-edge px-2 py-2 w-10">
              <button
                onClick={onRuleAdd}
                className="text-gray-600 hover:text-primary transition-colors flex items-center justify-center w-full"
                title="Adicionar regra"
              >
                <Plus className="w-4 h-4" />
              </button>
            </th>
          </tr>
        </thead>

        <tbody>
          {/* ── Conditions block ── */}
          <SectionRow label="Condições" span={colSpan} />

          {conditions.map(cond => (
            <tr key={cond.id}>
              <td className="sticky left-0 z-10 bg-surface border border-edge px-4 py-2">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-[10px] text-node-effect shrink-0 w-5">
                    C{cond.order}
                  </span>
                  <span className="text-xs text-gray-300 truncate max-w-[160px]" title={cond.label}>
                    {cond.label}
                  </span>
                </div>
              </td>

              {rules.map(rule => (
                <td key={rule.id} className="border border-edge px-3 py-2 bg-surface">
                  <ConditionToggle
                    value={rule.conditions[cond.id] ?? '—'}
                    onChange={v => onConditionValueChange(rule.id, cond.id, v)}
                  />
                </td>
              ))}

              <td className="border border-edge bg-surface" />
            </tr>
          ))}

          {/* ── Separator ── */}
          <tr>
            <td colSpan={colSpan} className="h-0.5 bg-primary border-none p-0" />
          </tr>

          {/* ── Effects block ── */}
          <SectionRow label="Efeitos" span={colSpan} />

          {effects.map(eff => (
            <tr key={eff.id}>
              <td className="sticky left-0 z-10 bg-surface border border-edge px-4 py-2">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-[10px] text-node-cause shrink-0 w-5">
                    E{eff.order}
                  </span>
                  <span className="text-xs text-gray-300 truncate max-w-[160px]" title={eff.label}>
                    {eff.label}
                  </span>
                </div>
              </td>

              {rules.map(rule => (
                <td key={rule.id} className="border border-edge px-3 py-2 bg-surface">
                  <EffectToggle
                    value={rule.effects[eff.id] ?? 'N'}
                    onChange={v => onEffectValueChange(rule.id, eff.id, v)}
                  />
                </td>
              ))}

              <td className="border border-edge bg-surface" />
            </tr>
          ))}

          {/* Empty-rules hint */}
          {rules.length === 0 && (
            <tr>
              <td
                colSpan={colSpan}
                className="border border-edge px-4 py-6 text-center text-xs text-gray-600 italic bg-surface"
              >
                Nenhuma regra gerada. Clique em{' '}
                <span className="not-italic font-medium text-gray-500">+</span> para adicionar
                manualmente.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
