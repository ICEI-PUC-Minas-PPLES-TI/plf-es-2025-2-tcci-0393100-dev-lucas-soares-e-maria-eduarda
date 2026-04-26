import { Plus, Trash2 } from 'lucide-react';
import type {
  ConditionValue,
  DecisionCondition,
  DecisionEffect,
  DecisionRule,
  EffectValue,
} from '../types/decisionTable';

// ─── Cell toggle buttons ──────────────────────────────────────────────────────

const CONDITION_OPTIONS: { v: ConditionValue; label: string; activeClass: string }[] = [
  { v: 'S', label: 'S', activeClass: 'bg-node-cause-bg text-white' },
  { v: 'N', label: 'N', activeClass: 'bg-node-error text-white' },
  { v: '—', label: '—', activeClass: 'bg-surface-elevated text-gray-400' },
];

function ConditionToggle({ value, onChange }: { value: ConditionValue; onChange: (v: ConditionValue) => void }) {
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

function EffectToggle({ value, onChange }: { value: EffectValue; onChange: (v: EffectValue) => void }) {
  return (
    <div className="flex gap-1 justify-center">
      {(['S', 'N'] as EffectValue[]).map(v => (
        <button
          key={v}
          onClick={() => onChange(v)}
          className={`w-7 h-7 rounded text-xs font-medium transition-colors ${
            value === v
              ? v === 'S' ? 'bg-node-cause-bg text-white' : 'bg-node-error text-white'
              : 'bg-surface-hover text-gray-600 hover:text-gray-300 hover:bg-surface-elevated'
          }`}
        >
          {v}
        </button>
      ))}
    </div>
  );
}

// ─── Read-only badges ─────────────────────────────────────────────────────────

function ConditionBadge({ value }: { value: ConditionValue }) {
  const cls =
    value === 'S' ? 'bg-node-cause-bg text-white' :
    value === 'N' ? 'bg-node-error text-white' :
    'bg-surface-elevated text-gray-400';
  return (
    <div className="flex justify-center">
      <span className={`w-7 h-7 rounded text-xs font-medium flex items-center justify-center ${cls}`}>
        {value}
      </span>
    </div>
  );
}

function EffectBadge({ value }: { value: EffectValue }) {
  const cls = value === 'S' ? 'bg-node-cause-bg text-white' : 'bg-node-error text-white';
  return (
    <div className="flex justify-center">
      <span className={`w-7 h-7 rounded text-xs font-medium flex items-center justify-center ${cls}`}>
        {value}
      </span>
    </div>
  );
}

// ─── Row height constants (kept in sync between both columns) ─────────────────

const H = {
  header:    'h-[41px]',
  section:   'h-[29px]',
  data:      'h-[45px]',
  separator: 'h-[2px]',
  hint:      'h-[72px]',
};

// ─── Main component ───────────────────────────────────────────────────────────

interface DecisionMatrixProps {
  conditions: DecisionCondition[];
  effects: DecisionEffect[];
  rules: DecisionRule[];
  onConditionValueChange: (ruleId: string, conditionId: string, value: ConditionValue) => void;
  onEffectValueChange: (ruleId: string, effectId: string, value: EffectValue) => void;
  onRuleAdd: () => void;
  onRuleDelete: (ruleId: string) => void;
  readOnly?: boolean;
}

export function DecisionMatrix({
  conditions,
  effects,
  rules,
  onConditionValueChange,
  onEffectValueChange,
  onRuleAdd,
  onRuleDelete,
  readOnly = true,
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

  const addColSpan = readOnly ? 0 : 1;
  const totalRuleCols = Math.max(rules.length + addColSpan, 1);

  return (
    /* Single scroll container — one scrollbar, correct behavior */
    <div className="flex-1 overflow-auto">
      <div className="flex min-w-max">

        {/* ── Sticky label column (div-level sticky, avoids table-cell z-index bugs) ── */}
        <div className="sticky left-0 z-20 shrink-0 bg-surface-card border-r border-edge">
          <table className="border-separate border-spacing-0 text-sm">
            <thead>
              <tr>
                <th className={`bg-surface-card border-b border-edge px-4 text-left w-55 min-w-55 ${H.header}`}>
                  <span className="text-[10px] text-gray-500 uppercase tracking-wider">Elemento</span>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td className={`bg-surface-elevated border-b border-edge px-4 text-[10px] text-gray-500 uppercase tracking-widest ${H.section}`}>
                  Condições
                </td>
              </tr>
              {conditions.map(cond => (
                <tr key={cond.id}>
                  <td className={`bg-surface border-b border-edge px-4 ${H.data}`}>
                    <div className="flex items-center gap-2 h-full">
                      <span className="font-mono text-[10px] text-node-effect shrink-0 w-5">C{cond.order}</span>
                      <span className="text-xs text-gray-300 truncate max-w-40" title={cond.label}>{cond.label}</span>
                    </div>
                  </td>
                </tr>
              ))}

              <tr><td className={`bg-primary border-none p-0 ${H.separator}`} /></tr>

              <tr>
                <td className={`bg-surface-elevated border-b border-edge px-4 text-[10px] text-gray-500 uppercase tracking-widest ${H.section}`}>
                  Efeitos
                </td>
              </tr>
              {effects.map(eff => (
                <tr key={eff.id}>
                  <td className={`bg-surface border-b border-edge px-4 ${H.data}`}>
                    <div className="flex items-center gap-2 h-full">
                      <span className="font-mono text-[10px] text-node-cause shrink-0 w-5">E{eff.order}</span>
                      <span className="text-xs text-gray-300 truncate max-w-40" title={eff.label}>{eff.label}</span>
                    </div>
                  </td>
                </tr>
              ))}

              {rules.length === 0 && (
                <tr><td className={`bg-surface border-b border-edge ${H.hint}`} /></tr>
              )}
            </tbody>
          </table>
        </div>

        {/* ── Rule columns ── */}
        <div className="flex-1">
          <table className="border-separate border-spacing-0 text-sm">
            <thead>
              <tr>
                {rules.map(rule => (
                  <th
                    key={rule.id}
                    className={`sticky top-0 bg-surface-card border-t border-r border-b border-edge px-3 min-w-32.5 text-center ${H.header}`}
                  >
                    <div className={`flex items-center gap-1 h-full ${readOnly ? 'justify-center' : 'justify-between'}`}>
                      <span className="text-xs text-gray-400 font-normal">R{rule.order}</span>
                      {!readOnly && (
                        <button
                          onClick={() => onRuleDelete(rule.id)}
                          className="text-gray-600 hover:text-node-error transition-colors"
                          title="Excluir regra"
                        >
                          <Trash2 className="w-3 h-3" />
                        </button>
                      )}
                    </div>
                  </th>
                ))}
                {!readOnly && (
                  <th className={`sticky top-0 bg-surface-card border-t border-r border-b border-edge px-2 w-10 ${H.header}`}>
                    <button
                      onClick={onRuleAdd}
                      className="text-gray-600 hover:text-primary transition-colors flex items-center justify-center w-full h-full"
                      title="Adicionar regra"
                    >
                      <Plus className="w-4 h-4" />
                    </button>
                  </th>
                )}
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colSpan={totalRuleCols} className={`bg-surface-elevated border-r border-b border-edge ${H.section}`} />
              </tr>

              {conditions.map(cond => (
                <tr key={cond.id}>
                  {rules.map(rule => (
                    <td key={rule.id} className={`border-r border-b border-edge px-3 bg-surface ${H.data}`}>
                      <div className="flex items-center justify-center h-full">
                        {readOnly
                          ? <ConditionBadge value={rule.conditions[cond.id] ?? '—'} />
                          : <ConditionToggle value={rule.conditions[cond.id] ?? '—'} onChange={v => onConditionValueChange(rule.id, cond.id, v)} />}
                      </div>
                    </td>
                  ))}
                  {!readOnly && <td className={`border-r border-b border-edge bg-surface ${H.data}`} />}
                </tr>
              ))}

              <tr>
                <td colSpan={totalRuleCols} className={`bg-primary border-none p-0 ${H.separator}`} />
              </tr>

              <tr>
                <td colSpan={totalRuleCols} className={`bg-surface-elevated border-r border-b border-edge ${H.section}`} />
              </tr>

              {effects.map(eff => (
                <tr key={eff.id}>
                  {rules.map(rule => (
                    <td key={rule.id} className={`border-r border-b border-edge px-3 bg-surface ${H.data}`}>
                      <div className="flex items-center justify-center h-full">
                        {readOnly
                          ? <EffectBadge value={rule.effects[eff.id] ?? 'N'} />
                          : <EffectToggle value={rule.effects[eff.id] ?? 'N'} onChange={v => onEffectValueChange(rule.id, eff.id, v)} />}
                      </div>
                    </td>
                  ))}
                  {!readOnly && <td className={`border-r border-b border-edge bg-surface ${H.data}`} />}
                </tr>
              ))}

              {rules.length === 0 && (
                <tr>
                  <td
                    colSpan={totalRuleCols}
                    className={`border-r border-b border-edge px-4 text-center text-xs text-gray-600 italic bg-surface ${H.hint}`}
                  >
                    Nenhuma regra gerada. Clique em{' '}
                    <span className="not-italic font-medium text-gray-500">+</span> para adicionar manualmente.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
}
