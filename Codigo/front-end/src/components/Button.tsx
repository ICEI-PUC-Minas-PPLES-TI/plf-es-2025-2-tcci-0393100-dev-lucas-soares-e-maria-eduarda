import type { ButtonHTMLAttributes, ReactNode } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'accent' | 'ghost' | 'danger' | 'danger-filled';
  size?: 'sm' | 'md' | 'lg';
  children: ReactNode;
}

const variants = {
  primary: 'bg-primary hover:bg-primary-hover text-white',
  secondary: 'bg-gray-800 hover:bg-gray-700 text-gray-100',
  outline: 'bg-surface-elevated border border-gray-700 text-gray-300 hover:border-primary',
  accent: 'bg-primary/10 hover:bg-primary text-primary-light hover:text-white',
  ghost: 'bg-transparent hover:bg-surface-elevated text-gray-300',
  danger: 'bg-transparent border border-red-900/50 text-red-400 hover:bg-red-950/30 hover:border-red-800',
  'danger-filled': 'bg-red-500/20 hover:bg-red-500/30 text-red-400',
};

const sizes = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2',
  lg: 'px-6 py-3',
};

export function Button({ variant = 'primary', size = 'md', className = '', children, ...props }: ButtonProps) {
  return (
    <button
      className={`rounded-lg transition-colors flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
