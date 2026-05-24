import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, XCircle, AlertCircle, ChevronRight, Loader2 } from 'lucide-react';
import { SectionHeader } from '../../../components/SectionHeader';
import GFCService from '../../../services/GFC/GFCService';
import DecisionTableService from '../../../services/DecisionTable/DecisionTableService';

interface ValidationWarningsProps {
  projectId: string;
}

type Severity = 'high' | 'medium' | 'low';

interface Warning {
  id: string;
  title: string;
  description: string;
  artifact: string;
  severity: Severity;
  href: string;
}

const severityConfig = {
  high: {
    icon: XCircle,
    iconColor: 'text-red-400',
    borderColor: 'border-red-900/50 hover:border-red-800',
    bgHover: 'hover:bg-red-950/20',
  },
  medium: {
    icon: AlertTriangle,
    iconColor: 'text-yellow-400',
    borderColor: 'border-yellow-900/50 hover:border-yellow-800',
    bgHover: 'hover:bg-yellow-950/20',
  },
  low: {
    icon: AlertCircle,
    iconColor: 'text-blue-400',
    borderColor: 'border-blue-900/50 hover:border-blue-800',
    bgHover: 'hover:bg-blue-950/20',
  },
};

export function ValidationWarnings({ projectId }: ValidationWarningsProps) {
  const navigate = useNavigate();
  const [warnings, setWarnings] = useState<Warning[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);

    (async () => {
      try {
        const [gfcs, tables] = await Promise.all([
          GFCService.listarPorProjeto(projectId),
          DecisionTableService.listarPorProjeto(projectId),
        ]);

        // Busca a complexidade em paralelo para cada GFC. Falhas individuais
        // são silenciadas (GFCs novos podem não ter cálculo ainda).
        const complexities = await Promise.all(
          gfcs.map((g) =>
            GFCService.obterComplexidade(g.id)
              .then((c) => ({ gfc: g, complexity: c }))
              .catch(() => ({ gfc: g, complexity: null })),
          ),
        );

        if (cancelled) return;

        const collected: Warning[] = [];

        // Warnings de complexidade ciclomática (apenas moderate/high).
        complexities.forEach(({ gfc, complexity }) => {
          if (!complexity) return;
          const v = complexity.cyclomaticComplexityByEdgesAndNodes;
          if (v <= 10) return;
          const severity: Severity = v > 15 ? 'high' : 'medium';
          const backendMessage = complexity.warnings.find((w) => w.includes('maior que'));
          collected.push({
            id: `gfc-${gfc.id}`,
            title: severity === 'high'
              ? `Complexidade ciclomática alta (V(G) = ${v})`
              : `Complexidade ciclomática elevada (V(G) = ${v})`,
            description: backendMessage ?? `O método ${gfc.name} possui complexidade ${v}, acima do limite recomendado.`,
            artifact: gfc.name,
            severity,
            href: `/projeto/${projectId}/gfc/${gfc.id}`,
          });
        });

        // Tabelas de decisão desatualizadas (syncStatus === 'STALE').
        tables.forEach((table) => {
          if (table.syncStatus !== 'STALE') return;
          collected.push({
            id: `table-${table.id}`,
            title: 'Tabela de decisão desatualizada',
            description: 'O GCE de origem foi modificado depois da última sincronização. Resincronize para refletir as mudanças.',
            artifact: table.name,
            severity: 'medium',
            href: `/projeto/${projectId}/gce/${table.gceId}/tabela-decisao`,
          });
        });

        // Ordena por severidade (high → medium → low) pra dar destaque ao mais crítico.
        const order: Record<Severity, number> = { high: 0, medium: 1, low: 2 };
        collected.sort((a, b) => order[a.severity] - order[b.severity]);

        setWarnings(collected);
      } catch {
        if (!cancelled) setWarnings([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [projectId]);

  return (
    <div className="bg-surface-card border border-edge rounded-lg p-5">
      <SectionHeader
        title="Avisos e Validações"
        action={
          loading ? (
            <span className="text-xs text-gray-500 flex items-center gap-1.5">
              <Loader2 className="w-3 h-3 animate-spin" />
              Carregando…
            </span>
          ) : (
            <span className="text-xs text-gray-500">
              {warnings.length} {warnings.length === 1 ? 'item' : 'itens'}
            </span>
          )
        }
      />

      {loading ? (
        <div className="space-y-2">
          {[0, 1, 2].map((i) => (
            <div
              key={i}
              className="bg-surface border border-edge rounded-lg p-4 h-20 animate-pulse"
              style={{ animationDelay: `${i * 80}ms` }}
            />
          ))}
        </div>
      ) : warnings.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <AlertCircle className="w-8 h-8 mx-auto mb-2 opacity-50" />
          <p className="text-sm">Nenhum aviso ou erro detectado</p>
        </div>
      ) : (
        <div className="space-y-2">
          {warnings.map((warning) => {
            const config = severityConfig[warning.severity];
            const Icon = config.icon;

            return (
              <button
                key={warning.id}
                onClick={() => navigate(warning.href)}
                className={`
                  w-full text-left bg-surface border rounded-lg p-4
                  ${config.borderColor} ${config.bgHover}
                  transition-all group cursor-pointer
                  flex items-start gap-3
                `}
              >
                <div className={`mt-0.5 ${config.iconColor}`}>
                  <Icon className="w-5 h-5" />
                </div>

                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-2 mb-1">
                    <h3 className="text-sm">{warning.title}</h3>
                    <ChevronRight className="w-4 h-4 text-gray-600 group-hover:text-primary-light transition-colors shrink-0" />
                  </div>
                  <p className="text-xs text-gray-400 mb-2">{warning.description}</p>
                  <div className="flex items-center gap-2">
                    <span className="text-xs text-gray-600">Artefato:</span>
                    <span className="text-xs text-primary-light">{warning.artifact}</span>
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
