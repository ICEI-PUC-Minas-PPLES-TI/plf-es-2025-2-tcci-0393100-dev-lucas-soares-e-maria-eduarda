import { BarChart3 } from 'lucide-react';
import type { GFCStats as Stats } from '../utils/gfcConverters';

interface GFCStatsProps {
  stats: Stats;
}

export function GFCStats({ stats }: GFCStatsProps) {
  return (
    <div className="bg-surface-card border border-edge rounded-lg p-3 shadow-lg min-w-[200px]">
      <div className="flex items-center gap-2 mb-2 text-gray-200">
        <BarChart3 className="w-3.5 h-3.5" />
        <span className="text-xs font-medium">Estatísticas</span>
      </div>

      <div className="space-y-1.5">
        <Row label="Nós" value={stats.nodeCount} />
        <Row label="Arestas" value={stats.edgeCount} />
        <Row label="Complexidade" value={stats.cyclomaticComplexity} highlight />
      </div>
    </div>
  );
}

function Row({ label, value, highlight }: { label: string; value: number; highlight?: boolean }) {
  return (
    <div className="flex items-center justify-between">
      <span className="text-xs text-gray-500">{label}</span>
      <span className={`text-xs font-mono ${highlight ? 'text-primary-light' : 'text-gray-200'}`}>
        {value}
      </span>
    </div>
  );
}
