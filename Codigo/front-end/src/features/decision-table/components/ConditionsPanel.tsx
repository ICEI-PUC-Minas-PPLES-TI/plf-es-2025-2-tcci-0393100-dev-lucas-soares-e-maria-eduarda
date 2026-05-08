import type { DecisionCondition, DecisionEffect } from '../types/decisionTable';

interface ConditionsPanelProps {
  conditions: DecisionCondition[];
  effects: DecisionEffect[];
}

function EmptyHint({ text }: { text: string }) {
  return <p className="text-xs text-gray-600 px-2 py-1 italic">{text}</p>;
}

export function ConditionsPanel({ conditions, effects }: ConditionsPanelProps) {
  return (
    <aside className="w-56 border-r border-edge bg-surface shrink-0 flex flex-col overflow-y-auto">
      <div className="p-4 flex flex-col gap-6">
        <p className="text-xs text-gray-500 uppercase tracking-wider">Estrutura</p>

        {/* Conditions */}
        <section>
          <h3 className="text-xs font-medium text-gray-400 uppercase tracking-wide mb-2 px-1">
            Condições
          </h3>
          <ul className="space-y-0.5">
            {conditions.length === 0 ? (
              <EmptyHint text="Nenhuma causa no GCE" />
            ) : (
              conditions.map(cond => (
                <li
                  key={cond.id}
                  className="flex items-start gap-2 px-2 py-1.5 rounded hover:bg-surface-hover"
                >
                  <span className="font-mono text-[10px] text-node-effect shrink-0 mt-0.5 w-5">
                    C{cond.order}
                  </span>
                  <span className="text-xs text-gray-300 leading-snug" title={cond.label}>
                    {cond.label}
                  </span>
                </li>
              ))
            )}
          </ul>
        </section>

        <div className="border-t border-edge" />

        {/* Effects */}
        <section>
          <h3 className="text-xs font-medium text-gray-400 uppercase tracking-wide mb-2 px-1">
            Efeitos
          </h3>
          <ul className="space-y-0.5">
            {effects.length === 0 ? (
              <EmptyHint text="Nenhum efeito no GCE" />
            ) : (
              effects.map(eff => (
                <li
                  key={eff.id}
                  className="flex items-start gap-2 px-2 py-1.5 rounded hover:bg-surface-hover"
                >
                  <span className="font-mono text-[10px] text-node-cause shrink-0 mt-0.5 w-5">
                    E{eff.order}
                  </span>
                  <span className="text-xs text-gray-300 leading-snug" title={eff.label}>
                    {eff.label}
                  </span>
                </li>
              ))
            )}
          </ul>
        </section>
      </div>
    </aside>
  );
}
