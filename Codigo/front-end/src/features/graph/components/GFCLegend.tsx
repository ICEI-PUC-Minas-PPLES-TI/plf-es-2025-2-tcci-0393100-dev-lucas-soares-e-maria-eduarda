export function GFCLegend() {
  const items = [
    { label: 'Início', shape: 'pill', color: 'var(--color-gfc-start-bg)' },
    { label: 'Comando', shape: 'square', color: 'var(--color-gfc-statement-bg)' },
    { label: 'Decisão', shape: 'diamond', color: 'var(--color-gfc-decision-bg)' },
    { label: 'Retorno', shape: 'square', color: 'var(--color-gfc-return-bg)' },
    { label: 'Fim', shape: 'pill', color: 'var(--color-gfc-end-bg)' },
  ];

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-3 shadow-lg">
      <div className="text-xs text-gray-400 mb-2 font-medium">Legenda</div>
      <div className="space-y-1.5">
        {items.map((item) => (
          <div key={item.label} className="flex items-center gap-2">
            <Shape kind={item.shape} color={item.color} />
            <span className="text-xs text-gray-300">{item.label}</span>
          </div>
        ))}
        <div className="pt-1.5 mt-1.5 border-t border-edge space-y-1">
          <div className="flex items-center gap-2">
            <span className="w-4 h-0.5" style={{ background: 'var(--color-gfc-edge-true)' }} />
            <span className="text-xs text-gray-300">true</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="w-4 h-0.5" style={{ background: 'var(--color-gfc-edge-false)' }} />
            <span className="text-xs text-gray-300">false</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="w-4 h-0.5" style={{ background: 'var(--color-gfc-edge-loop)' }} />
            <span className="text-xs text-gray-300">loop</span>
          </div>
        </div>
      </div>
    </div>
  );
}

function Shape({ kind, color }: { kind: string; color: string }) {
  if (kind === 'diamond') {
    return <span className="w-3.5 h-3.5 rotate-45 border border-edge" style={{ background: color }} />;
  }
  if (kind === 'pill') {
    return <span className="w-4 h-3 rounded-full border border-edge" style={{ background: color }} />;
  }
  return <span className="w-3.5 h-3.5 rounded border border-edge" style={{ background: color }} />;
}
