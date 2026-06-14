interface CardGridSkeletonProps {
  count?: number;
}

export function CardGridSkeleton({ count = 8 }: CardGridSkeletonProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      {Array.from({ length: count }).map((_, i) => (
        <div
          key={i}
          className="bg-surface-card border border-edge rounded-lg p-4 flex flex-col gap-3 animate-pulse"
          style={{ animationDelay: `${i * 70}ms` }}
        >
          <div className="flex items-start justify-between">
            <div className="w-10 h-10 rounded bg-surface-hover" />
            <div className="w-4 h-4 rounded bg-surface-hover" />
          </div>
          <div className="flex-1 space-y-2">
            <div className="h-4 bg-surface-hover rounded w-3/4" />
            <div className="h-3 bg-surface-hover rounded w-1/2" />
            <div className="h-3 bg-surface-hover rounded w-2/3" />
          </div>
          <div className="h-8 bg-surface-hover rounded w-full" />
        </div>
      ))}
    </div>
  );
}
