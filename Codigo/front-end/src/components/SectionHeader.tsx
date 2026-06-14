import type { ComponentType, ReactNode } from 'react';

interface SectionHeaderProps {
  title: string;
  icon?: ComponentType<{ className?: string }>;
  action?: ReactNode;
}

export function SectionHeader({ title, icon: Icon, action }: SectionHeaderProps) {
  return (
    <div className="flex items-center justify-between mb-4">
      <div className="flex items-center gap-2">
        {Icon && <Icon className="w-5 h-5 text-primary-light" />}
        <h2 className="text-lg">{title}</h2>
      </div>
      {action}
    </div>
  );
}
