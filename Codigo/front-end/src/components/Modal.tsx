import type { ReactNode } from 'react';
import { X } from 'lucide-react';

interface ModalProps {
  title: string;
  onClose: () => void;
  maxWidth?: 'sm' | 'md' | 'lg';
  children: ReactNode;
}

const maxWidths = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
};

export function Modal({ title, onClose, maxWidth = 'md', children }: ModalProps) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/60" onClick={onClose} />

      <div className={`relative bg-surface-elevated border border-edge rounded-lg w-full ${maxWidths[maxWidth]} mx-4 p-6`}>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-100">{title}</h2>
          <button
            onClick={onClose}
            className="p-1 hover:bg-surface-hover rounded transition-colors"
          >
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>

        {children}
      </div>
    </div>
  );
}
