import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, Network, Trash2 } from 'lucide-react';
import { Button } from '../../../components/Button';
import { ConfirmModal } from '../../../components/ConfirmModal';
import { ARTIFACT_TYPES } from '../../../shared/artifactTypes';
import DecisionTableService from '../../../services/DecisionTable/DecisionTableService';
import GCEService from '../../../services/GCE/GCEService';
import { mapDTOToDecisionTable } from '../utils/decisionTableMapper';
import type { DecisionTable } from '../types/decisionTable';

interface DecisionTableListProps {
  projectId: string;
}

export function DecisionTableList({ projectId }: DecisionTableListProps) {
  const navigate = useNavigate();
  const [tables, setTables] = useState<DecisionTable[]>([]);
  const [gceNames, setGceNames] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState<DecisionTable | null>(null);

  useEffect(() => {
    Promise.all([
      DecisionTableService.listarPorProjeto(projectId),
      GCEService.listarPorProjeto(projectId),
    ]).then(([dtos, gces]) => {
      setTables(dtos.map(mapDTOToDecisionTable));
      setGceNames(Object.fromEntries(gces.map((g) => [g.id, g.name])));
    }).finally(() => setLoading(false));
  }, [projectId]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await DecisionTableService.deletar(deleteTarget.id);
    setTables((prev) => prev.filter((t) => t.id !== deleteTarget.id));
    setDeleteTarget(null);
  };

  const tableConfig = ARTIFACT_TYPES['TABLE'];
  const TableIcon = tableConfig.icon;

  return (
    <div className="container mx-auto px-6 py-6">
      <div className="mb-4">
        <h2 className="text-base font-semibold text-gray-200">Tabelas de Decisão</h2>
        <p className="text-sm text-gray-500 mt-0.5">
          {loading
            ? 'Carregando...'
            : `${tables.length} tabela${tables.length !== 1 ? 's' : ''}`}
        </p>
      </div>

      {!loading && tables.length === 0 ? (
        <div className="bg-surface-card border border-edge rounded-lg p-12 flex flex-col items-center gap-3">
          <div className={`w-12 h-12 rounded-lg ${tableConfig.bgColor} flex items-center justify-center`}>
            <TableIcon className={`w-6 h-6 ${tableConfig.color}`} />
          </div>
          <p className="text-sm text-gray-400">Nenhuma tabela de decisão gerada ainda.</p>
          <p className="text-xs text-gray-600 text-center max-w-xs">
            Abra um GCE, valide-o e clique em "Gerar Tabela" para criar sua primeira tabela de decisão.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {tables.map((table) => (
            <div
              key={table.id}
              className="bg-surface-card border border-edge rounded-lg p-4 flex flex-col gap-3 hover:border-edge-hover transition-colors"
            >
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-3 min-w-0 flex-1">
                  <div className={`w-10 h-10 rounded ${tableConfig.bgColor} border border-edge flex items-center justify-center ${tableConfig.color} shrink-0`}>
                    <TableIcon className="w-5 h-5" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="text-sm font-medium text-gray-200 truncate" title={table.name}>
                      {table.name}
                    </h3>
                    <div className="flex items-center gap-1.5 mt-1">
                      <Network className="w-3 h-3 text-gray-600 shrink-0" />
                      <span className="text-xs text-gray-500 truncate">
                        {gceNames[table.gceId] ?? 'GCE vinculado'}
                      </span>
                      {table.syncStatus === 'STALE' && (
                        <span className="inline-flex items-center gap-0.5 text-xs text-yellow-500">
                          <AlertTriangle className="w-3 h-3" />
                          Desatualizada
                        </span>
                      )}
                    </div>
                  </div>
                </div>
                <button
                  onClick={() => setDeleteTarget(table)}
                  className="text-gray-600 hover:text-red-400 transition-colors p-1 -mr-1 shrink-0"
                  title="Excluir tabela"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>

              <div className="text-xs text-gray-600 space-y-0.5">
                <p>{table.conditions.length} condição{table.conditions.length !== 1 ? 'ões' : ''} · {table.effects.length} efeito{table.effects.length !== 1 ? 's' : ''} · {table.rules.length} regra{table.rules.length !== 1 ? 's' : ''}</p>
                {table.updatedAt && table.updatedAt !== table.generatedAt && (
                  <p>Atualizada em {new Date(table.updatedAt).toLocaleDateString('pt-BR')}</p>
                )}
              </div>

              <Button
                size="sm"
                className="w-full justify-center"
                onClick={() => navigate(`/projeto/${projectId}/gce/${table.gceId}/tabela-decisao`)}
              >
                Abrir
              </Button>
            </div>
          ))}
        </div>
      )}

      {deleteTarget && (
        <ConfirmModal
          title="Excluir tabela de decisão"
          message={
            <>
              Tem certeza que deseja excluir a tabela{' '}
              <span className="text-gray-200 font-medium">{deleteTarget.name}</span>? Essa ação não pode ser desfeita.
            </>
          }
          icon={AlertTriangle}
          confirmLabel="Excluir"
          confirmLoadingLabel="Excluindo..."
          onClose={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  );
}
