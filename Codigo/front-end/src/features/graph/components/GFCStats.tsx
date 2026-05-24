import { useState } from 'react';
import { Activity, AlertTriangle, ChevronDown, ChevronUp, Info } from 'lucide-react';
import type { GFCStats as Stats } from '../utils/gfcConverters';
import type { GFCCyclomaticComplexityDTO } from '../types/gfc';

interface GFCStatsProps {
  stats: Stats;
  complexity: GFCCyclomaticComplexityDTO | null;
}

type Severity = 'simple' | 'moderate' | 'high';

function severityFor(value: number): Severity {
  if (value > 15) return 'high';
  if (value > 10) return 'moderate';
  return 'simple';
}

interface SeverityStyle {
  color: string;
  bg: string;
  ring: string;
  label: string;
  warningText: string;
  warningBg: string;
  warningBorder: string;
}

const SEVERITY_STYLES: Record<Severity, SeverityStyle> = {
  simple: {
    color: 'text-green-400', bg: 'bg-green-500/10', ring: 'ring-green-500/30', label: 'Simples',
    warningText: 'text-green-300/90', warningBg: 'bg-green-500/5', warningBorder: 'border-green-500/20',
  },
  moderate: {
    color: 'text-yellow-400', bg: 'bg-yellow-500/10', ring: 'ring-yellow-500/30', label: 'Moderada',
    warningText: 'text-yellow-300/90', warningBg: 'bg-yellow-500/5', warningBorder: 'border-yellow-500/20',
  },
  high: {
    color: 'text-red-400', bg: 'bg-red-500/10', ring: 'ring-red-500/30', label: 'Alta',
    warningText: 'text-red-300/90', warningBg: 'bg-red-500/5', warningBorder: 'border-red-500/20',
  },
};

export function GFCStats({ stats, complexity }: GFCStatsProps) {
  const [showFormulas, setShowFormulas] = useState(false);

  // Prefere o cálculo do backend; cai pro local quando o endpoint falha.
  const value = complexity?.cyclomaticComplexityByEdgesAndNodes ?? stats.cyclomaticComplexity;
  const severity = severityFor(value);
  const sev = SEVERITY_STYLES[severity];

  const nodes = complexity?.nodesCount ?? stats.nodeCount;
  const edges = complexity?.edgesCount ?? stats.edgeCount;
  const predicates = complexity?.predicateNodesCount;
  const warnings = complexity?.warnings ?? [];

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-3 shadow-lg w-52">
      <div className="flex items-center gap-2 mb-2.5 text-gray-200">
        <Activity className="w-3.5 h-3.5" />
        <span className="text-xs font-medium">Complexidade Ciclomática</span>
      </div>

      <div
        className={`flex items-center justify-between gap-2 ${sev.bg} ${sev.color} rounded-md px-2.5 py-2 ring-1 ${sev.ring}`}
        title={`V(G) = ${value} — ${sev.label}`}
      >
        <span className="text-[10px] uppercase tracking-wider opacity-80">{sev.label}</span>
        <span className="text-2xl font-mono font-semibold tabular-nums leading-none">{value}</span>
      </div>

      <div className="grid grid-cols-3 gap-1 mt-3">
        <Metric label="Nós" value={nodes} />
        <Metric label="Arestas" value={edges} />
        <Metric
          label="Predicados"
          value={predicates ?? '—'}
          title={predicates != null ? 'Nós de decisão (if/loop/ternary/switch)' : 'Não disponível'}
        />
      </div>

      {complexity && (
        <button
          onClick={() => setShowFormulas((v) => !v)}
          className="w-full flex items-center justify-between text-[10px] text-gray-500 hover:text-gray-300 transition-colors mt-2.5 px-0.5"
        >
          <span className="flex items-center gap-1">
            <Info className="w-3 h-3" />
            Fórmulas
          </span>
          {showFormulas ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
        </button>
      )}

      {showFormulas && complexity && (
        <div className="mt-1.5 space-y-1 text-[10px] font-mono text-gray-400 bg-surface border border-edge rounded px-2 py-1.5">
          <div>
            {complexity.formulaByEdgesAndNodes} ={' '}
            <span className="text-gray-200">{complexity.cyclomaticComplexityByEdgesAndNodes}</span>
          </div>
          <div>
            {complexity.formulaByPredicateNodes} ={' '}
            <span className="text-gray-200">{complexity.cyclomaticComplexityByPredicateNodes}</span>
          </div>
        </div>
      )}

      {warnings.length > 0 && (
        <div className="mt-2.5 space-y-1.5">
          {warnings.map((w, i) => (
            <div
              key={i}
              className={`flex items-start gap-1.5 text-[10px] ${sev.warningText} ${sev.warningBg} border ${sev.warningBorder} rounded px-2 py-1.5 leading-snug`}
            >
              <AlertTriangle className="w-3 h-3 shrink-0 mt-0.5" />
              <span>{w}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function Metric({
  label,
  value,
  title,
}: {
  label: string;
  value: number | string;
  title?: string;
}) {
  return (
    <div
      className="flex flex-col items-center bg-surface border border-edge rounded px-1.5 py-1"
      title={title}
    >
      <span className="text-sm font-mono text-gray-200 tabular-nums leading-tight">{value}</span>
      <span className="text-[9px] text-gray-500 uppercase tracking-wider mt-0.5">{label}</span>
    </div>
  );
}
