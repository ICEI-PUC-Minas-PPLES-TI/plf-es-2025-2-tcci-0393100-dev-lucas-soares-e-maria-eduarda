import { Circle, Square, Trash2 } from 'lucide-react';
import type { OperatorType } from '../types/gce';

interface ElementPaletteProps {
  onAddNode: (type: 'CAUSE' | 'EFFECT' | 'OPERATOR', operatorType?: OperatorType) => void;
  onDelete: () => void;
  hasSelection: boolean;
}

const elements = [
  { id: 'causa', icon: Circle, label: 'Causa', color: 'var(--color-node-cause)', action: 'CAUSE' as const },
  { id: 'efeito', icon: Square, label: 'Efeito', color: 'var(--color-node-effect)', action: 'EFFECT' as const },
];

const operators: { id: string; label: string; operatorType: OperatorType }[] = [
  { id: 'and', label: 'AND', operatorType: 'AND' },
  { id: 'or', label: 'OR', operatorType: 'OR' },
  { id: 'not', label: 'NOT', operatorType: 'NOT' },
];

export function ElementPalette({ onAddNode, onDelete, hasSelection }: ElementPaletteProps) {
  return (
    <div className="w-56 bg-surface border-r border-edge flex flex-col shrink-0 overflow-y-auto">
      <div className="p-4 border-b border-edge">
        <h3 className="text-sm text-edge-hover mb-3">Elementos</h3>
        <div className="space-y-1">
          {elements.map((el) => {
            const Icon = el.icon;
            return (
              <button
                key={el.id}
                onClick={() => onAddNode(el.action)}
                className="w-full flex items-center gap-3 px-3 py-2 rounded text-sm text-gray-300 hover:bg-surface-hover transition-colors"
              >
                <Icon className="w-4 h-4" style={{ color: el.color }} />
                {el.label}
              </button>
            );
          })}
        </div>
      </div>

      <div className="p-4 border-b border-edge">
        <h3 className="text-sm text-edge-hover mb-3">Operadores Logicos</h3>
        <div className="space-y-1">
          {operators.map((op) => (
            <button
              key={op.id}
              onClick={() => onAddNode('OPERATOR', op.operatorType)}
              className="w-full flex items-center gap-3 px-3 py-2 rounded text-sm text-gray-300 hover:bg-surface-hover transition-colors"
            >
              <div className="w-4 h-4 flex items-center justify-center">
                <div className="w-3 h-3 rounded-sm" style={{ background: 'var(--color-node-operator)' }} />
              </div>
              {op.label}
            </button>
          ))}
        </div>
      </div>

      <div className="p-4 border-b border-edge">
        <button
          onClick={onDelete}
          disabled={!hasSelection}
          className={`w-full flex items-center gap-3 px-3 py-2 rounded text-sm transition-colors ${
            hasSelection
              ? 'text-red-400 hover:bg-surface-hover'
              : 'text-gray-600 cursor-not-allowed'
          }`}
        >
          <Trash2 className="w-4 h-4" />
          Excluir selecionado
        </button>
      </div>

      <div className="mt-auto p-4 border-t border-edge">
        <h3 className="text-xs text-edge-hover mb-2">Legenda</h3>
        <div className="space-y-2 text-xs text-gray-400">
          <div className="flex items-center gap-2">
            <Circle className="w-3 h-3" style={{ color: 'var(--color-node-cause)' }} />
            <span>Causa</span>
          </div>
          <div className="flex items-center gap-2">
            <Square className="w-3 h-3" style={{ color: 'var(--color-node-effect)' }} />
            <span>Efeito</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-sm" style={{ background: 'var(--color-node-operator)' }} />
            <span>Operador</span>
          </div>
        </div>
      </div>
    </div>
  );
}
