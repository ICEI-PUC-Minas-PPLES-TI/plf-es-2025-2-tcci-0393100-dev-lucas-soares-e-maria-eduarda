import type { ButtonHTMLAttributes, ReactNode } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'accent';
  children: ReactNode;
}

const variants = {
  primary: 'bg-primary hover:bg-primary-hover text-white',
  secondary: 'bg-gray-800 hover:bg-gray-700 text-gray-100',
  outline: 'bg-surface-elevated border border-gray-700 text-gray-300 hover:border-primary',
  accent: 'bg-primary/10 hover:bg-primary text-primary-light hover:text-white',
};

export function Button({ variant = 'primary', className = '', children, ...props }: ButtonProps) {
  return (
    <button
      className={`px-4 py-2 rounded-lg transition-colors flex items-center gap-2 ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
