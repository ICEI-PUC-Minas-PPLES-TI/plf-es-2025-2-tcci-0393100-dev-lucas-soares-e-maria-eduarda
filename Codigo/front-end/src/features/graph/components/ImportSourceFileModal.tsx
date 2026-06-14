import { useState, type SyntheticEvent } from 'react';
import { Upload, FileCode, Check } from 'lucide-react';
import { Modal } from '../../../components/Modal';
import { Button } from '../../../components/Button';
import SourceFileService from '../../../services/GFC/SourceFileService';
import { extractApiErrorMessage } from '../../../utils/apiError';

interface ImportSourceFileModalProps {
  projectId: string;
  onClose: () => void;
  onImported?: (sourceFileId: string) => void;
}

export function ImportSourceFileModal({ projectId, onClose, onImported }: ImportSourceFileModalProps) {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [importedId, setImportedId] = useState<string | null>(null);

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    if (!selectedFile) {
      setError('Selecione um arquivo .java.');
      return;
    }
    setUploading(true);
    setError(null);
    try {
      const { id } = await SourceFileService.upload(projectId, selectedFile);
      setImportedId(id);
      onImported?.(id);
    } catch (err) {
      setError(extractApiErrorMessage(err, 'Erro ao enviar o arquivo.'));
    } finally {
      setUploading(false);
    }
  };

  if (importedId) {
    return (
      <Modal title="Arquivo importado" onClose={onClose} maxWidth="md">
        <div className="flex flex-col items-center text-center py-2 space-y-3">
          <div className="w-12 h-12 rounded-full bg-green-500/10 flex items-center justify-center text-green-400">
            <Check className="w-6 h-6" />
          </div>
          <p className="text-sm text-gray-200">
            <span className="font-mono">{selectedFile?.name}</span> foi importado com sucesso.
          </p>
          <p className="text-xs text-gray-500">
            O arquivo já aparece na aba <span className="text-gray-300">Artefatos de Código</span> do projeto.
          </p>
          <Button className="w-full justify-center mt-2" onClick={onClose}>
            Fechar
          </Button>
        </div>
      </Modal>
    );
  }

  return (
    <Modal title="Importar arquivo" onClose={onClose} maxWidth="md">
      <form onSubmit={handleSubmit} className="space-y-4">
        <p className="text-sm text-gray-400">
          Envie um arquivo Java pro projeto. Você poderá gerar GFCs a partir dele depois.
        </p>

        <label
          htmlFor="import-source-file"
          className="flex flex-col items-center justify-center gap-2 p-6 border-2 border-dashed border-edge rounded-lg cursor-pointer hover:border-primary transition-colors"
        >
          {selectedFile ? (
            <FileCode className="w-6 h-6 text-primary-light" />
          ) : (
            <Upload className="w-6 h-6 text-gray-500" />
          )}
          <span className="text-sm text-gray-300 font-mono">
            {selectedFile ? selectedFile.name : 'Clique para selecionar'}
          </span>
          <span className="text-xs text-gray-500">Apenas arquivos .java</span>
          <input
            id="import-source-file"
            type="file"
            accept=".java"
            className="hidden"
            onChange={(e) => {
              setSelectedFile(e.target.files?.[0] ?? null);
              setError(null);
            }}
          />
        </label>

        {error && <p className="text-sm text-red-400 text-center">{error}</p>}

        <div className="flex gap-3 pt-2">
          <Button
            type="button"
            variant="ghost"
            className="flex-1 justify-center"
            onClick={onClose}
            disabled={uploading}
          >
            Cancelar
          </Button>
          <Button
            type="submit"
            className="flex-1 justify-center"
            disabled={!selectedFile || uploading}
          >
            {uploading ? 'Enviando...' : 'Importar'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
