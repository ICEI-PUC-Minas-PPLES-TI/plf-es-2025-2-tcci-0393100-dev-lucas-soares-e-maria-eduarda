import { useState, type ComponentType } from 'react';
import { Modal } from './Modal';
import { Button } from './Button';

interface ConfirmModalProps {
  title: string;
  message: string | React.ReactNode;
  icon?: ComponentType<{ className?: string }>;
  iconColor?: string;
  iconBg?: string;
  confirmLabel?: string;
  confirmLoadingLabel?: string;
  confirmVariant?: 'primary' | 'danger-filled';
  onClose: () => void;
  onConfirm: () => Promise<void>;
}

export function ConfirmModal({
  title,
  message,
  icon: Icon,
  iconColor = 'text-red-400',
  iconBg = 'bg-red-500/10',
  confirmLabel = 'Confirmar',
  confirmLoadingLabel = 'Aguarde...',
  confirmVariant = 'danger-filled',
  onClose,
  onConfirm,
}: ConfirmModalProps) {
  const [loading, setLoading] = useState(false);

  const handleConfirm = async () => {
    setLoading(true);
    await onConfirm();
    setLoading(false);
  };

  return (
    <Modal title={title} onClose={onClose} maxWidth="sm">
      <div className="flex flex-col items-center text-center -mt-2">
        {Icon && (
          <div className={`p-3 ${iconBg} rounded-full mb-4`}>
            <Icon className={`w-6 h-6 ${iconColor}`} />
          </div>
        )}

        <div className="text-sm text-gray-400 mb-6">{message}</div>

        <div className="flex gap-3 w-full">
          <Button
            type="button"
            variant="ghost"
            className="flex-1 justify-center"
            onClick={onClose}
          >
            Cancelar
          </Button>
          <Button
            type="button"
            variant={confirmVariant}
            disabled={loading}
            className="flex-1 justify-center"
            onClick={handleConfirm}
          >
            {loading ? confirmLoadingLabel : confirmLabel}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
