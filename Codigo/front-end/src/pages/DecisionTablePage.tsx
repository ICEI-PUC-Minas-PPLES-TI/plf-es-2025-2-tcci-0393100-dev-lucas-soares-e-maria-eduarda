import { useState, useCallback, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Header } from '../components/Header';
import { DecisionTableToolbar } from '../features/decision-table/components/DecisionTableToolbar';
import { ConditionsPanel } from '../features/decision-table/components/ConditionsPanel';
import { DecisionMatrix } from '../features/decision-table/components/DecisionMatrix';
import { ValidationStatusBar } from '../features/decision-table/components/ValidationStatusBar';
import { convertGCEToDecisionTable, createEmptyRule } from '../features/decision-table/utils/gceToDecisionTable';
import decisionTableLocalService from '../features/decision-table/services/decisionTableLocalService';
import GCEService from '../services/GCE/GCEService';
import ProjectService from '../services/Project/ProjectService';
import type { DecisionTable, ConditionValue, EffectValue } from '../features/decision-table/types/decisionTable';

export function DecisionTablePage() {
  const { projectId, gceId } = useParams<{ projectId: string; gceId: string }>();
  const navigate = useNavigate();

  const [table, setTable] = useState<DecisionTable | null>(null);
  const [gceName, setGceName] = useState<string | null>(null);
  const [projectName, setProjectName] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [saveStatus, setSaveStatus] = useState<'idle' | 'saving' | 'saved' | 'error'>('idle');

  useEffect(() => {
    if (!gceId || !projectId) return;

    async function load() {
      try {
        const gce = await GCEService.buscarPorId(gceId!);
        setGceName(gce.name);

        const saved = decisionTableLocalService.getByGceId(gceId!);
        if (saved) {
          setTable(saved);
        } else {
          const generated = convertGCEToDecisionTable(gce);
          const newTable: DecisionTable = {
            ...generated,
            id: gceId!,
            updatedAt: generated.generatedAt,
          };
          setTable(newTable);
        }
      } catch {
        setLoadError('Erro ao carregar o GCE.');
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [gceId, projectId]);

  useEffect(() => {
    if (!projectId) return;
    ProjectService.buscarPorId(projectId)
      .then((p) => setProjectName(p.name))
      .catch(() => {});
  }, [projectId]);

  const handleSave = useCallback(() => {
    if (!table) return;
    setSaveStatus('saving');
    try {
      const updated = decisionTableLocalService.save(table);
      setTable(updated);
      setSaveStatus('saved');
      setTimeout(() => setSaveStatus('idle'), 2000);
    } catch {
      setSaveStatus('error');
      setTimeout(() => setSaveStatus('idle'), 3000);
    }
  }, [table]);

  const handleRegenerate = useCallback(async () => {
    if (!gceId) return;
    try {
      const gce = await GCEService.buscarPorId(gceId);
      setGceName(gce.name);
      const generated = convertGCEToDecisionTable(gce);
      const newTable: DecisionTable = {
        ...generated,
        id: gceId,
        updatedAt: generated.generatedAt,
      };
      setTable(newTable);
      setSaveStatus('idle');
    } catch {
      // silently ignore — user can retry
    }
  }, [gceId]);

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

  if (loading) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-gray-400">Carregando tabela de decisão...</p>
      </div>
    );
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
          { label: projectName ?? 'Projeto', href: `/projeto/${projectId}` },
          { label: table.name },
        ]}
      />

      <DecisionTableToolbar
        tableName={table.name}
        gceName={gceName ?? ''}
        saveStatus={saveStatus}
        onBack={handleBack}
        onRegenerate={handleRegenerate}
        onSave={handleSave}
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
    </div>
  );
}
