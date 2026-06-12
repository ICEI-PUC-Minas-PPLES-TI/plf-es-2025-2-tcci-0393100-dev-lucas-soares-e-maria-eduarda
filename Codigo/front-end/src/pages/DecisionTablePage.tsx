import { useState, useCallback, useEffect } from 'react';
import { useParams, useNavigate, useOutletContext } from 'react-router-dom';
import { Header } from '../components/Header';
import { DecisionTableToolbar } from '../features/decision-table/components/DecisionTableToolbar';
import { ConditionsPanel } from '../features/decision-table/components/ConditionsPanel';
import { DecisionMatrix } from '../features/decision-table/components/DecisionMatrix';
import { ValidationStatusBar } from '../features/decision-table/components/ValidationStatusBar';
import { createEmptyRule } from '../features/decision-table/utils/gceToDecisionTable';
import { mapDTOToDecisionTable } from '../features/decision-table/utils/decisionTableMapper';
import DecisionTableService from '../services/DecisionTable/DecisionTableService';
import GCEService from '../services/GCE/GCEService';
import { TestSignatureModal } from '../features/graph/components/TestSignatureModal';
import { DecisionTablePageSkeleton } from '../features/decision-table/components/DecisionTablePageSkeleton';
import { extractApiErrorMessage } from '../utils/apiError';
import type { DecisionTable, ConditionValue, EffectValue } from '../features/decision-table/types/decisionTable';
import type { GenerateFunctionalTestSignatureResponseDTO } from '../features/decision-table/types/decisionTableDTO';
import type { ProjectLayoutContext } from './ProjectLayout';

export function DecisionTablePage() {
  const { projectId, gceId } = useParams<{ projectId: string; gceId: string }>();
  const { project } = useOutletContext<ProjectLayoutContext>();
  const navigate = useNavigate();

  const [table, setTable] = useState<DecisionTable | null>(null);
  const [gceName, setGceName] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [saveStatus, setSaveStatus] = useState<'idle' | 'saving' | 'saved' | 'error'>('idle');
  const [regenerateStatus, setRegenerateStatus] = useState<'idle' | 'loading' | 'synced' | 'error'>('idle');
  const [testModalOpen, setTestModalOpen] = useState(false);
  const [testLoading, setTestLoading] = useState(false);
  const [testError, setTestError] = useState<string | null>(null);
  const [testData, setTestData] = useState<GenerateFunctionalTestSignatureResponseDTO | null>(null);

  useEffect(() => {
    if (!gceId || !projectId) return;

    async function load() {
      try {
        const [gce, dto] = await Promise.all([
          GCEService.buscarPorId(projectId!, gceId!),
          DecisionTableService.buscarPorGceId(projectId!, gceId!).catch(async (err) => {
            if (err?.response?.status === 404) {
              return DecisionTableService.criarAPartirDoGCE(projectId!, gceId!);
            }
            throw err;
          }),
        ]);

        setGceName(gce.name);
        setTable(mapDTOToDecisionTable(dto));
      } catch {
        setLoadError('Erro ao carregar a tabela de decisão.');
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [gceId, projectId]);

  const handleSave = useCallback(async () => {
    if (!table || !projectId) return;
    setSaveStatus('saving');
    try {
      const dto = await DecisionTableService.atualizar(projectId, table.id, {
        name: table.name,
        description: table.description,
      });
      setTable(mapDTOToDecisionTable(dto));
      setSaveStatus('saved');
      setTimeout(() => setSaveStatus('idle'), 2000);
    } catch {
      setSaveStatus('error');
      setTimeout(() => setSaveStatus('idle'), 3000);
    }
  }, [table, projectId]);

  const handleRegenerate = useCallback(async () => {
    if (!gceId || !projectId) return;
    setRegenerateStatus('loading');
    try {
      const dto = await DecisionTableService.sincronizar(projectId, gceId);
      setTable(mapDTOToDecisionTable(dto));
      setSaveStatus('idle');
      setRegenerateStatus('idle');
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status;
      if (status === 409) {
        setRegenerateStatus('synced');
      } else {
        setRegenerateStatus('error');
      }
      setTimeout(() => setRegenerateStatus('idle'), 3000);
    }
  }, [gceId, projectId]);

  const handleConditionValueChange = useCallback(
    (ruleId: string, conditionId: string, value: ConditionValue) => {
      setTable((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          rules: prev.rules.map((r) =>
            r.id === ruleId
              ? { ...r, conditions: { ...r.conditions, [conditionId]: value } }
              : r,
          ),
        };
      });
    },
    [],
  );

  const handleEffectValueChange = useCallback(
    (ruleId: string, effectId: string, value: EffectValue) => {
      setTable((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          rules: prev.rules.map((r) =>
            r.id === ruleId
              ? { ...r, effects: { ...r.effects, [effectId]: value } }
              : r,
          ),
        };
      });
    },
    [],
  );

  const handleRuleAdd = useCallback(() => {
    setTable((prev) => {
      if (!prev) return prev;
      const order = prev.rules.length + 1;
      const newRule = createEmptyRule(prev.conditions, prev.effects, order);
      return { ...prev, rules: [...prev.rules, newRule] };
    });
  }, []);

  const handleRuleDelete = useCallback((ruleId: string) => {
    setTable((prev) => {
      if (!prev) return prev;
      const filtered = prev.rules.filter((r) => r.id !== ruleId);
      const reordered = filtered.map((r, i) => ({ ...r, order: i + 1 }));
      return { ...prev, rules: reordered };
    });
  }, []);

  const handleBack = useCallback(() => {
    navigate(`/projeto/${projectId}/gce/${gceId}`);
  }, [navigate, projectId, gceId]);

  const handleGenerateTests = useCallback(async () => {
    if (!table || !projectId) return;
    setTestModalOpen(true);
    setTestLoading(true);
    setTestError(null);
    setTestData(null);
    try {
      const data = await DecisionTableService.gerarAssinaturaTesteFuncional(projectId, table.id);
      setTestData(data);
    } catch (err) {
      setTestError(extractApiErrorMessage(err, 'Erro ao gerar assinaturas de teste.'));
    } finally {
      setTestLoading(false);
    }
  }, [table, projectId]);

  if (loading) {
    return <DecisionTablePageSkeleton projectName={project.name} projectId={projectId ?? ''} />;
  }

  if (loadError || !table) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-red-400">{loadError ?? 'Tabela não encontrada.'}</p>
      </div>
    );
  }

  return (
    <div className="h-screen flex flex-col bg-surface">
      <Header
        breadcrumb={[
          { label: 'Projetos', href: '/homepage' },
          { label: project.name, href: `/projeto/${projectId}` },
          { label: table.name },
        ]}
      />

      <DecisionTableToolbar
        tableName={table.name}
        gceName={gceName ?? ''}
        saveStatus={saveStatus}
        syncStatus={table.syncStatus}
        onBack={handleBack}
        onRegenerate={handleRegenerate}
        regenerateStatus={regenerateStatus}
        onSave={handleSave}
        onGenerateTests={handleGenerateTests}
        generateTestsLoading={testLoading}
      />

      <div className="flex-1 flex overflow-hidden">
        <ConditionsPanel conditions={table.conditions} effects={table.effects} />
        <DecisionMatrix
          conditions={table.conditions}
          effects={table.effects}
          rules={table.rules}
          onConditionValueChange={handleConditionValueChange}
          onEffectValueChange={handleEffectValueChange}
          onRuleAdd={handleRuleAdd}
          onRuleDelete={handleRuleDelete}

        />
      </div>

      <ValidationStatusBar
        conditionCount={table.conditions.length}
        effectCount={table.effects.length}
        ruleCount={table.rules.length}
        generatedAt={table.generatedAt}
        updatedAt={table.updatedAt !== table.generatedAt ? table.updatedAt : undefined}
      />

      <TestSignatureModal
        open={testModalOpen}
        onClose={() => setTestModalOpen(false)}
        title={testData?.decisionTableName ?? table.name}
        subtitle={
          testData
            ? `${testData.testMethods.length} ${testData.testMethods.length === 1 ? 'teste' : 'testes'} · ${testData.rulesCount} regra${testData.rulesCount === 1 ? '' : 's'}`
            : null
        }
        generatedCode={testData?.generatedCode ?? null}
        loading={testLoading}
        error={testError}
        methods={testData?.testMethods.map((m) => ({ methodName: m.methodName, badge: m.ruleCode }))}
        warnings={testData?.warnings}
      />
    </div>
  );
}
