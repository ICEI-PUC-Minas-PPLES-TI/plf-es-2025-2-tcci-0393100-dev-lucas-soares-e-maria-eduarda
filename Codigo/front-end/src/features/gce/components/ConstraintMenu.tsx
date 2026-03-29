import { useEffect, useLayoutEffect, useRef } from 'react';
import { ShieldAlert, CheckCircle2, ArrowRight, Ban } from 'lucide-react';
import type { ComponentType } from 'react';
import type { RestrictionType } from '../types/gce';

interface ConstraintOption {
  id: RestrictionType;
  label: string;
  description: string;
  symbol: string;
  icon: ComponentType<{ className?: string }>;
  minNodes: number;
  maxNodes?: number;
}

interface ConstraintMenuProps {
  position: { x: number; y: number };
  selectedCount: number;
  onSelectConstraint: (type: RestrictionType) => void;
  onClose: () => void;
}

const options: ConstraintOption[] = [
  { id: 'EXCLUSIVE', label: 'Exclusivo (E)', description: 'no maximo 1', symbol: 'E', icon: ShieldAlert, minNodes: 2 },
  { id: 'INCLUSIVE', label: 'Inclusivo (I)', description: 'no minimo 1', symbol: 'I', icon: CheckCircle2, minNodes: 2 },
  { id: 'ONLY_ONE', label: 'Somente um (O)', description: 'exatamente 1', symbol: 'O', icon: CheckCircle2, minNodes: 2 },
  { id: 'REQUIRES', label: 'Exige (R)', description: 'c1 -> c2', symbol: 'R', icon: ArrowRight, minNodes: 2, maxNodes: 2 },
  { id: 'MASK', label: 'Mascara (M)', description: 'e1 -> !e2', symbol: 'M', icon: Ban, minNodes: 2, maxNodes: 2 },
];

export function ConstraintMenu({ position, selectedCount, onSelectConstraint, onClose }: ConstraintMenuProps) {
  const menuRef = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    const el = menuRef.current;
    if (!el) return;

    const rect = el.getBoundingClientRect();
    const gap = 16;

    let left = position.x + gap;
    let top = position.y - rect.height / 2;

    if (left + rect.width > window.innerWidth) {
      left = position.x - rect.width - gap;
    }
    
    if (top < 8) top = 8;
    if (top + rect.height > window.innerHeight) top = window.innerHeight - rect.height - 8;

    el.style.left = `${left}px`;
    el.style.top = `${top}px`;
  }, [position]);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };

    const handleClickOutside = (e: MouseEvent) => {
      const target = e.target as HTMLElement;
      if (!target.closest('[data-constraint-menu]')) onClose();
    };

    document.addEventListener('keydown', handleEscape);
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [onClose]);

  const isDisabled = (opt: ConstraintOption) => {
    if (selectedCount < opt.minNodes) return true;
    if (opt.maxNodes && selectedCount > opt.maxNodes) return true;
    return false;
  };

  return (
    <div
      ref={menuRef}
      data-constraint-menu
      className="fixed z-50 rounded-lg shadow-2xl border border-edge bg-surface-card overflow-hidden"
      style={{
        left: position.x,
        top: position.y,
        minWidth: 220,
      }}
    >
      <div className="px-4 py-3 border-b border-edge bg-surface">
        <h3 className="text-sm font-semibold text-white">Criar restricao</h3>
        <p className="text-xs text-gray-500 mt-1">{selectedCount} nos selecionados</p>
      </div>

      <div className="py-1">
        {options.map((opt) => {
          const disabled = isDisabled(opt);
          const Icon = opt.icon;
          return (
            <button
              key={opt.id}
              disabled={disabled}
              onClick={() => !disabled && onSelectConstraint(opt.id)}
              className={`w-full px-4 py-3 flex items-center gap-3 transition-colors ${
                disabled ? 'opacity-40 cursor-not-allowed' : 'hover:bg-surface-hover cursor-pointer'
              }`}
            >
              <div className={`w-8 h-8 rounded flex items-center justify-center shrink-0 ${
                disabled ? 'bg-surface-hover' : 'bg-primary/10'
              }`}>
                <Icon className={`w-4 h-4 ${disabled ? 'text-gray-600' : 'text-primary-light'}`} />
              </div>

              <div className="flex-1 text-left">
                <span className={`text-sm font-medium ${disabled ? 'text-gray-600' : 'text-white'}`}>
                  {opt.label}
                </span>
                <span className={`text-xs block ${disabled ? 'text-gray-700' : 'text-gray-500'}`}>
                  {opt.description}
                </span>
              </div>

              <div className={`w-6 h-6 rounded flex items-center justify-center text-xs font-bold ${
                disabled
                  ? 'bg-surface text-gray-600'
                  : 'bg-surface-hover text-primary-light border border-edge'
              }`}>
                {opt.symbol}
              </div>
            </button>
          );
        })}
      </div>

      <div className="px-4 py-2 border-t border-edge bg-surface">
        <p className="text-xs text-gray-600">Clique em uma opcao para aplicar</p>
      </div>
    </div>
  );
}
