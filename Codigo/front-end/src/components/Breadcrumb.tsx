import { Fragment } from 'react';
import { ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

interface BreadcrumbItem {
  label: string;
  href?: string;
}

interface BreadcrumbProps {
  items: BreadcrumbItem[];
}

export function Breadcrumb({ items }: BreadcrumbProps) {
  return (
    <nav className="flex items-center gap-2 text-sm">
      {items.map((item, index) => {
        const isLast = index === items.length - 1;

        return (
          <Fragment key={item.label}>
            {index > 0 && <ChevronRight className="w-4 h-4 text-gray-600" />}
            {item.href ? (
              <Link to={item.href} className="text-primary-light hover:text-cyan-300 transition-colors">
                {item.label}
              </Link>
            ) : (
              <span className={isLast ? 'text-gray-300' : 'text-gray-400'}>
                {item.label}
              </span>
            )}
          </Fragment>
        );
      })}
    </nav>
  );
}
