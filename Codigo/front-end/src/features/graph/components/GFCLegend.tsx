import { useState } from 'react';
import { ChevronDown, ChevronUp } from 'lucide-react';

const NODE_ITEMS = [
  { label: 'Início', shape: 'pill', color: 'var(--color-gfc-start-bg)' },
  { label: 'Comando', shape: 'square', color: 'var(--color-gfc-statement-bg)' },
  { label: 'Decisão', shape: 'diamond', color: 'var(--color-gfc-decision-bg)' },
  { label: 'Ternário', shape: 'diamond-sm', color: 'var(--color-gfc-ternary-bg)' },
  { label: 'Loop', shape: 'hex', color: 'var(--color-gfc-loop-bg)' },
  { label: 'Switch', shape: 'trapezoid', color: 'var(--color-gfc-switch-bg)' },
  { label: 'Case', shape: 'square', color: 'var(--color-gfc-case-bg)' },
  { label: 'Corpo do Case', shape: 'square', color: 'var(--color-gfc-case-block-bg)' },
  { label: 'Try', shape: 'square', color: 'var(--color-gfc-try-bg)' },
  { label: 'Catch', shape: 'square', color: 'var(--color-gfc-catch-bg)' },
  { label: 'Finally', shape: 'square', color: 'var(--color-gfc-finally-bg)' },
  { label: 'Retorno', shape: 'square', color: 'var(--color-gfc-return-bg)' },
  { label: 'Break', shape: 'pill', color: 'var(--color-gfc-break-bg)' },
  { label: 'Continue', shape: 'pill', color: 'var(--color-gfc-continue-bg)' },
  { label: 'Throw', shape: 'pill', color: 'var(--color-gfc-throw-bg)' },
  { label: 'Fim', shape: 'pill', color: 'var(--color-gfc-end-bg)' },
];

const EDGE_ITEMS = [
  { label: 'true', color: 'var(--color-gfc-edge-true)', dashed: false },
  { label: 'false', color: 'var(--color-gfc-edge-false)', dashed: false },
  { label: 'loop', color: 'var(--color-gfc-edge-loop)', dashed: true },
  { label: 'body', color: 'var(--color-gfc-edge-body)', dashed: false },
  { label: 'exit', color: 'var(--color-gfc-edge-exit)', dashed: false },
  { label: 'case', color: 'var(--color-gfc-edge-case)', dashed: false },
  { label: 'default', color: 'var(--color-gfc-edge-default)', dashed: true },
  { label: 'try', color: 'var(--color-gfc-edge-try)', dashed: false },
  { label: 'catch', color: 'var(--color-gfc-edge-catch)', dashed: false },
  { label: 'finally', color: 'var(--color-gfc-edge-finally)', dashed: false },
  { label: 'break', color: 'var(--color-gfc-edge-break)', dashed: true },
  { label: 'continue', color: 'var(--color-gfc-edge-continue)', dashed: true },
  { label: 'throw', color: 'var(--color-gfc-edge-throw)', dashed: true },
];

export function GFCLegend() {
  const [expanded, setExpanded] = useState(false);
  const nodes = expanded ? NODE_ITEMS : NODE_ITEMS.slice(0, 5);
  const edges = expanded ? EDGE_ITEMS : EDGE_ITEMS.slice(0, 3);

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-3 shadow-lg w-44 flex flex-col max-h-[70vh]">
      <button
        onClick={() => setExpanded((v) => !v)}
        className="w-full flex items-center justify-between text-xs text-gray-400 mb-2 font-medium hover:text-gray-200 transition-colors shrink-0"
      >
        <span>Legenda</span>
        {expanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
      </button>
      <div className="space-y-1.5 overflow-y-auto pr-1 -mr-1 thin-scrollbar">
        {nodes.map((item) => (
          <div key={item.label} className="flex items-center gap-2">
            <Shape kind={item.shape} color={item.color} />
            <span className="text-xs text-gray-300">{item.label}</span>
          </div>
        ))}
        <div className="pt-1.5 mt-1.5 border-t border-edge space-y-1">
          {edges.map((item) => (
            <div key={item.label} className="flex items-center gap-2">
              <span
                className="w-4 h-0.5"
                style={
                  item.dashed
                    ? { backgroundImage: `linear-gradient(to right, ${item.color} 60%, transparent 0%)`, backgroundSize: '4px 2px', backgroundRepeat: 'repeat-x' }
                    : { background: item.color }
                }
              />
              <span className="text-xs text-gray-300">{item.label}</span>
            </div>
          ))}
        </div>
        {!expanded && (
          <button
            onClick={() => setExpanded(true)}
            className="text-xs text-primary-light hover:text-primary transition-colors mt-1"
          >
            Ver todos
          </button>
        )}
      </div>
    </div>
  );
}

function Shape({ kind, color }: { kind: string; color: string }) {
  if (kind === 'diamond') {
    return <span className="w-3.5 h-3.5 rotate-45 border border-edge" style={{ background: color }} />;
  }
  if (kind === 'diamond-sm') {
    return <span className="w-2.5 h-2.5 rotate-45 border border-edge" style={{ background: color }} />;
  }
  if (kind === 'pill') {
    return <span className="w-4 h-3 rounded-full border border-edge" style={{ background: color }} />;
  }
  if (kind === 'hex') {
    return (
      <span
        className="w-4 h-3 border border-edge"
        style={{
          background: color,
          clipPath: 'polygon(20% 0, 80% 0, 100% 50%, 80% 100%, 20% 100%, 0 50%)',
        }}
      />
    );
  }
  if (kind === 'trapezoid') {
    return (
      <span
        className="w-4 h-3 border border-edge"
        style={{
          background: color,
          clipPath: 'polygon(20% 0, 80% 0, 100% 100%, 0 100%)',
        }}
      />
    );
  }
  return <span className="w-3.5 h-3.5 rounded border border-edge" style={{ background: color }} />;
}
