import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';

interface PasswordFieldProps {
  id: string;
  label: string;
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  required?: boolean;
}

export function PasswordField({ id, label, placeholder, value, onChange, required }: PasswordFieldProps) {
  const [show, setShow] = useState(false);

  return (
    <div className="space-y-2">
      <label htmlFor={id} className="block text-sm text-gray-300">
        {label}
      </label>
      <div className="relative">
        <input
          id={id}
          type={show ? 'text' : 'password'}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          className="w-full px-4 py-3 pr-12 bg-[#1a2332] border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-cyan-500 focus:ring-2 focus:ring-cyan-500/20 transition-all duration-200"
          required={required}
        />
        <button
          type="button"
          onClick={() => setShow(!show)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-cyan-400 transition-colors duration-200"
        >
          {show ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
        </button>
      </div>
    </div>
  );
}