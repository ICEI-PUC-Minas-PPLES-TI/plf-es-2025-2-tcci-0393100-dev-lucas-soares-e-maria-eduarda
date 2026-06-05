import { Loader2, Network } from 'lucide-react';
import { Header } from '../../../components/Header';

interface GCEEditorSkeletonProps {
  projectName: string;
  projectId: string;
}

/**
 * Esqueleto da `GCEEditorPage`. Mantém a estrutura header + toolbar + palette
 * + canvas pra evitar o "flash" de tela em branco no primeiro load.
 */
export function GCEEditorSkeleton({ projectName, projectId }: GCEEditorSkeletonProps) {
  return (
    <div className="h-screen flex flex-col bg-surface">
      <Header
        breadcrumb={[
          { label: 'Projetos', href: '/homepage' },
          { label: projectName, href: `/projeto/${projectId}` },
          { label: 'Carregando…' },
        ]}
      />

      <div className="h-12 bg-surface-card border-b border-edge flex items-center justify-between px-4 shrink-0">
        <div className="flex items-center gap-3 min-w-0">
          <div className="h-3.5 w-40 rounded bg-surface-hover animate-pulse" />
        </div>
        <div className="flex items-center gap-2">
          <div className="h-7 w-24 rounded bg-surface-hover animate-pulse" />
          <div className="h-7 w-24 rounded bg-surface-hover animate-pulse" />
          <div className="h-7 w-28 rounded bg-surface-hover animate-pulse" />
        </div>
      </div>

      <div className="h-0.5 bg-primary/20 overflow-hidden relative shrink-0">
        <div className="absolute inset-y-0 w-1/3 bg-primary animate-[gfc-progress_1.1s_ease-in-out_infinite]" />
      </div>

      <div className="flex-1 flex overflow-hidden">
        <div className="w-56 bg-surface border-r border-edge shrink-0 flex flex-col p-3 gap-3">
          <div className="h-3.5 w-28 rounded bg-surface-hover animate-pulse" />
          <div className="space-y-2 mt-1">
            {[0, 1, 2, 3].map((i) => (
              <div
                key={i}
                className="h-12 w-full rounded-lg bg-surface-hover animate-pulse"
                style={{ animationDelay: `${i * 80}ms` }}
              />
            ))}
          </div>
        </div>

        <div className="flex-1 relative bg-surface flex items-center justify-center">
          <div className="flex flex-col items-center gap-4">
            <div className="relative w-16 h-16 flex items-center justify-center">
              <div className="absolute inset-0 rounded-full bg-primary/10 animate-pulse" />
              <Loader2 className="absolute inset-0 w-16 h-16 text-primary/60 animate-spin" strokeWidth={1.2} />
              <Network className="w-7 h-7 text-primary-light relative z-10" />
            </div>
            <div className="flex flex-col items-center gap-1.5">
              <p className="text-sm text-gray-300 font-medium">Carregando GCE</p>
              <p className="text-xs text-gray-500">Buscando nós, arestas e restrições…</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
