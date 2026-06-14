import { Header } from '../../../components/Header';

const TAB_WIDTHS = ['5rem', '9rem', '11rem', '10rem', '8.5rem'];

export function ProjectPageSkeleton() {
  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <Header breadcrumb={[{ label: 'Projetos', href: '/homepage' }]} />

      {/* Barra do projeto (ProjectHeader) */}
      <div className="container mx-auto px-6 py-4 flex items-center justify-between border-b border-edge">
        <div className="flex-1 space-y-2">
          <div className="h-7 w-72 bg-surface-card rounded animate-pulse" />
          <div className="h-4 w-96 max-w-full bg-surface-card rounded animate-pulse" />
          <div className="h-3 w-56 bg-surface-card rounded animate-pulse" />
        </div>
        <div className="flex items-center gap-3">
          <div className="h-9 w-36 bg-surface-card rounded-lg animate-pulse" />
          <div className="h-9 w-36 bg-surface-card rounded-lg animate-pulse" />
        </div>
      </div>

      {/* Faixa de abas (ProjectTabs) */}
      <div className="container mx-auto px-6 flex gap-1 border-b border-edge">
        {TAB_WIDTHS.map((width, i) => (
          <div key={i} className="px-4 py-3 flex items-center gap-2">
            <div className="w-4 h-4 bg-surface-card rounded animate-pulse" style={{ animationDelay: `${i * 70}ms` }} />
            <div className="h-4 bg-surface-card rounded animate-pulse" style={{ width, animationDelay: `${i * 70}ms` }} />
          </div>
        ))}
      </div>

      {/* Conteúdo (layout da Visão Geral) */}
      <main className="flex-1">
        <div className="container mx-auto px-6 py-6 flex gap-6">
          <div className="flex-1 space-y-6 min-w-0">
            <div className="h-28 bg-surface-card border border-edge rounded-lg animate-pulse" />
            <div className="h-48 bg-surface-card border border-edge rounded-lg animate-pulse" />
          </div>
          <aside className="w-80 shrink-0 hidden lg:block">
            <div className="h-64 bg-surface-card border border-edge rounded-lg animate-pulse" />
          </aside>
        </div>
      </main>
    </div>
  );
}
