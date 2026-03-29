import { useState, useCallback, useRef, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { ReactFlowProvider } from '@xyflow/react';
import { Header } from '../components/Header';
import { GCEToolbar } from '../features/gce/components/GCEToolbar';
import { ElementPalette } from '../features/gce/components/ElementPalette';
import { GCECanvas, type GCECanvasHandle } from '../features/gce/components/GCECanvas';
import { PropertiesPanel } from '../features/gce/components/PropertiesPanel';
import { ValidationPanel } from '../features/gce/components/ValidationPanel';
import {
  dtoToFlowNodes,
  dtoToFlowEdges,
  dtoToRestrictions,
  flowToCreateRequest,
  savePositions,
} from '../features/gce/utils/gceConverters';
import GCEService from '../services/GCE/GCEService';
import ProjectService from '../services/Project/ProjectService';
import type { GCEDTO, GCEValidationResponse, GCERestriction } from '../features/gce/types/gce';

export function GCEEditorPage() {
  const { projectId, gceId } = useParams<{ projectId: string; gceId: string }>();
  const location = useLocation();
  const navigate = useNavigate();

  const isNew = gceId === 'new';
  const routeState = location.state as { name?: string; description?: string } | null;

  const [gce, setGce] = useState<GCEDTO | null>(
    isNew && projectId
      ? {
          id: 'new',
          projectId,
          name: routeState?.name ?? 'Novo GCE',
          description: routeState?.description ?? '',
          selected: false,
          nodes: [],
          edges: [],
          restrictions: [],
        }
      : null,
  );
  const [loading, setLoading] = useState(!isNew);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [selectedEdgeId, setSelectedEdgeId] = useState<string | null>(null);
  const [showValidation, setShowValidation] = useState(false);
  const [validationResult, setValidationResult] = useState<GCEValidationResponse | null>(null);
  const [saveStatus, setSaveStatus] = useState<'idle' | 'saving' | 'saved' | 'error'>('idle');
  const [liveRestrictions, setLiveRestrictions] = useState<GCERestriction[]>([]);
  const [projectName, setProjectName] = useState<string | null>(null);
  const canvasRef = useRef<GCECanvasHandle>(null);

  useEffect(() => {
    if (isNew || !gceId) return;
    GCEService.buscarPorId(gceId)
      .then(setGce)
      .catch(() => setLoadError('GCE não encontrado.'))
      .finally(() => setLoading(false));
  }, [isNew, gceId]);

  useEffect(() => {
    if (!projectId) return;
    ProjectService.buscarPorId(projectId)
      .then((p) => setProjectName(p.name))
      .catch(() => {});
  }, [projectId]);

  const handleSelectionChange = useCallback((nodeId: string | null, edgeId: string | null) => {
    setSelectedNodeId(nodeId);
    setSelectedEdgeId(edgeId);
  }, []);

  const handleSave = useCallback(async () => {
    if (!gce || !projectId) return;
    const state = canvasRef.current?.getState();
    if (!state) return;

    setSaveStatus('saving');
    try {
      const request = flowToCreateRequest(gce, state.nodes, state.edges, state.restrictions);

      if (gce.id === 'new') {
        // Primeiro save: cria no backend
        const { id } = await GCEService.criar(request);
        const created = await GCEService.buscarPorId(id);
        savePositions(id, state.nodes);
        setGce(created);
        // Atualiza a URL sem recarregar o componente
        navigate(`/projeto/${projectId}/gce/${id}`, { replace: true, state: null });
      } else {
        // Saves seguintes: atualiza
        savePositions(gce.id, state.nodes);
        const updated = await GCEService.atualizar(gce.id, request);
        setGce(updated);
      }

      setSaveStatus('saved');
      setTimeout(() => setSaveStatus('idle'), 2000);
    } catch {
      setSaveStatus('error');
      setTimeout(() => setSaveStatus('idle'), 3000);
    }
  }, [gce, projectId, navigate]);

  const handleNameChange = useCallback(async (newName: string) => {
    if (!gce || !projectId) return;

    const updatedGce = { ...gce, name: newName };
    setGce(updatedGce);

    if (gce.id === 'new') return;

    const state = canvasRef.current?.getState();
    if (!state) return;

    setSaveStatus('saving');
    try {
      savePositions(gce.id, state.nodes);
      const request = flowToCreateRequest(updatedGce, state.nodes, state.edges, state.restrictions);
      const updated = await GCEService.atualizar(gce.id, request);
      setGce(updated);
      setSaveStatus('saved');
      setTimeout(() => setSaveStatus('idle'), 2000);
    } catch {
      setSaveStatus('error');
      setTimeout(() => setSaveStatus('idle'), 3000);
    }
  }, [gce, projectId]);

  const handleValidate = useCallback(async () => {
    if (!gce || gce.id === 'new') return;
    setValidationResult(null);
    setShowValidation(true);
    try {
      const result = await GCEService.validar(gce.id);
      setValidationResult(result);
    } catch {
      setValidationResult({
        valid: false,
        errors: [{ code: 'NET', message: 'Erro ao conectar com o servidor.' }],
        warnings: [],
      });
    }
  }, [gce]);

  if (loading) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-gray-400">Carregando GCE...</p>
      </div>
    );
  }

  if (loadError || !gce) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-red-400">{loadError ?? 'GCE não encontrado.'}</p>
      </div>
    );
  }

  return (
    <ReactFlowProvider>
      <div className="h-screen flex flex-col bg-surface">
        <Header
          breadcrumb={[
            { label: 'Projetos', href: '/homepage' },
            { label: projectName ?? 'Projeto', href: `/projeto/${projectId}` },
            { label: gce.name },
          ]}
        />

        <GCEToolbar
          gceName={gce.name}
          onSave={handleSave}
          onValidate={handleValidate}
          onGenerateTable={() => {}}
          onNameChange={handleNameChange}
          saveStatus={saveStatus}
          canValidate={gce.id !== 'new'}
        />

        <div className="flex-1 flex overflow-hidden">
          <ElementPalette
            onAddNode={(type, op) => canvasRef.current?.addNode(type, op)}
            onDelete={() => canvasRef.current?.deleteSelected()}
            hasSelection={canvasRef.current?.hasSelection ?? false}
          />

          <div className="flex-1 flex flex-col relative">
            <GCECanvas
              ref={canvasRef}
              initialNodes={dtoToFlowNodes(gce)}
              initialEdges={dtoToFlowEdges(gce)}
              initialRestrictions={dtoToRestrictions(gce)}
              onSelectionChange={handleSelectionChange}
              onRestrictionsChange={setLiveRestrictions}
            />

            {showValidation && (
              <ValidationPanel
                result={validationResult}
                onClose={() => setShowValidation(false)}
              />
            )}
          </div>

          <PropertiesPanel
            selectedNodeId={selectedNodeId}
            selectedEdgeId={selectedEdgeId}
            restrictions={liveRestrictions}
          />
        </div>
      </div>
    </ReactFlowProvider>
  );
}
