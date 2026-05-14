import { useState, useEffect, useMemo, useCallback } from 'react';
import { useParams, useNavigate, useOutletContext } from 'react-router-dom';
import { ReactFlowProvider } from '@xyflow/react';
import { AlertTriangle } from 'lucide-react';
import { Header } from '../components/Header';
import { ConfirmModal } from '../components/ConfirmModal';
import { GFCToolbar } from '../features/graph/components/GFCToolbar';
import { GFCCanvas } from '../features/graph/components/GFCCanvas';
import { MethodPanel } from '../features/graph/components/MethodPanel';
import { NodeInfoPanel } from '../features/graph/components/NodeInfoPanel';
import { SourceFileViewerModal } from '../features/graph/components/SourceFileViewerModal';
import { MethodCodeViewerModal } from '../features/graph/components/MethodCodeViewerModal';
import {
  dtoToFlowNodes,
  dtoToFlowEdges,
  computeStats,
} from '../features/graph/utils/gfcConverters';
import GFCService from '../services/GFC/GFCService';
import SourceFileService from '../services/GFC/SourceFileService';
import type {
  GFCDTO,
  GFCSummaryDTO,
  GFCSourceMethodDTO,
  GFCSourceFileDTO,
} from '../features/graph/types/gfc';
import type { ProjectLayoutContext } from './ProjectLayout';

export function GFCViewerPage() {
  const { projectId, gfcId } = useParams<{ projectId: string; gfcId: string }>();
  const { project } = useOutletContext<ProjectLayoutContext>();
  const navigate = useNavigate();

  const [gfc, setGfc] = useState<GFCDTO | null>(null);
  const [sourceFile, setSourceFile] = useState<GFCSourceFileDTO | null>(null);
  const [methods, setMethods] = useState<GFCSourceMethodDTO[]>([]);
  const [projectGfcs, setProjectGfcs] = useState<GFCSummaryDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [generatingSignature, setGeneratingSignature] = useState<string | null>(null);
  const [generateError, setGenerateError] = useState<string | null>(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showSourceModal, setShowSourceModal] = useState(false);
  const [showMethodModal, setShowMethodModal] = useState(false);

  const [methodPanelCollapsed, setMethodPanelCollapsed] = useState(false);
  const [nodeInfoCollapsed, setNodeInfoCollapsed] = useState(false);
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);

  useEffect(() => {
    if (!gfcId) return;
    let cancelled = false;
    setLoading(true);
    setLoadError(null);

    GFCService.buscarPorId(gfcId)
      .then(async (g) => {
        if (cancelled) return;
        setGfc(g);
        const [file, fileMethods, gfcs] = await Promise.all([
          SourceFileService.buscarPorId(g.sourceFileId),
          SourceFileService.listarMetodos(g.sourceFileId),
          GFCService.listarPorProjeto(g.projectId),
        ]);
        if (cancelled) return;
        setSourceFile(file);
        setMethods(fileMethods);
        setProjectGfcs(gfcs);
      })
      .catch(() => {
        if (!cancelled) setLoadError('GFC não encontrado.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [gfcId]);

  const flowNodes = useMemo(() => (gfc ? dtoToFlowNodes(gfc) : []), [gfc]);
  const flowEdges = useMemo(() => (gfc ? dtoToFlowEdges(gfc) : []), [gfc]);
  const stats = useMemo(
    () => (gfc ? computeStats(gfc) : { nodeCount: 0, edgeCount: 0, cyclomaticComplexity: 0 }),
    [gfc],
  );

  const selectedNodeInfo = useMemo(() => {
    if (!gfc || !selectedNodeId) return null;
    const node = gfc.nodes.find((n) => n.code === selectedNodeId);
    if (!node) return null;
    return {
      code: node.code,
      label: node.label,
      nodeType: node.type,
      startLine: node.startLine,
      endLine: node.endLine,
    };
  }, [gfc, selectedNodeId]);

  const handleSelectMethod = useCallback(
    async (method: GFCSourceMethodDTO) => {
      if (!projectId || !gfc) return;

      const existing = projectGfcs.find((g) => g.methodSignature === method.signature);
      if (existing) {
        if (existing.id === gfc.id) return;
        navigate(`/projeto/${projectId}/gfc/${existing.id}`);
        return;
      }

      setGeneratingSignature(method.signature);
      setGenerateError(null);
      try {
        const { id } = await GFCService.criar({
          projectId,
          sourceFileId: gfc.sourceFileId,
          methodSignature: method.signature,
          name: method.name,
        });
        navigate(`/projeto/${projectId}/gfc/${id}`);
      } catch {
        setGenerateError('Erro ao gerar GFC para esse método.');
      } finally {
        setGeneratingSignature(null);
      }
    },
    [projectId, gfc, projectGfcs, navigate],
  );

  const handleDelete = async () => {
    if (!gfc || !projectId) return;
    await GFCService.deletar(gfc.id);
    navigate(`/projeto/${projectId}`);
  };

  if (loading && !gfc) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-gray-400">Carregando GFC...</p>
      </div>
    );
  }

  if ((loadError && !gfc) || !gfc) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <p className="text-red-400">{loadError ?? 'GFC não encontrado.'}</p>
      </div>
    );
  }

  const transitioning = loading && !!gfc;

  return (
    <ReactFlowProvider>
      <div className="h-screen flex flex-col bg-surface">
        <Header
          breadcrumb={[
            { label: 'Projetos', href: '/homepage' },
            { label: project.name, href: `/projeto/${projectId}` },
            { label: gfc.name },
          ]}
        />

        <GFCToolbar
          gfcName={gfc.name}
          methodSignature={gfc.methodSignature}
          onDelete={() => setShowDeleteModal(true)}
          canDelete
          onViewSource={() => setShowSourceModal(true)}
          canViewSource={!!sourceFile}
          onViewMethod={() => setShowMethodModal(true)}
          canViewMethod={!!sourceFile && !!gfc.methodSignature}
        />

        {transitioning && (
          <div className="h-0.5 bg-primary/20 overflow-hidden relative shrink-0">
            <div className="absolute inset-y-0 w-1/3 bg-primary animate-[gfc-progress_1.1s_ease-in-out_infinite]" />
          </div>
        )}

        <div
          className={`flex-1 flex overflow-hidden transition-opacity ${transitioning ? 'opacity-60 pointer-events-none' : 'opacity-100'}`}
        >
          <MethodPanel
            isCollapsed={methodPanelCollapsed}
            onToggleCollapse={() => setMethodPanelCollapsed((v) => !v)}
            methods={methods}
            gfcs={projectGfcs}
            currentMethodSignature={gfc.methodSignature}
            generatingSignature={generatingSignature}
            onSelectMethod={handleSelectMethod}
            fileName={sourceFile?.fileName ?? null}
          />

          <GFCCanvas
            key={gfc.id}
            gfcId={gfc.id}
            initialNodes={flowNodes}
            initialEdges={flowEdges}
            stats={stats}
            onNodeSelect={setSelectedNodeId}
          />

          <NodeInfoPanel
            node={selectedNodeInfo}
            isCollapsed={nodeInfoCollapsed}
            onToggleCollapse={() => setNodeInfoCollapsed((v) => !v)}
          />
        </div>

        {generateError && (
          <div className="fixed bottom-4 right-4 bg-red-900 text-red-200 px-4 py-2 rounded">
            {generateError}
          </div>
        )}

        {showDeleteModal && (
          <ConfirmModal
            title="Excluir GFC"
            message={
              <>
                Tem certeza que deseja excluir o GFC{' '}
                <span className="text-gray-200 font-medium">{gfc.name}</span>? Essa ação não pode
                ser desfeita.
              </>
            }
            icon={AlertTriangle}
            confirmLabel="Excluir"
            confirmLoadingLabel="Excluindo..."
            onClose={() => setShowDeleteModal(false)}
            onConfirm={handleDelete}
          />
        )}

        {showMethodModal && sourceFile && gfc.methodSignature && (
          <MethodCodeViewerModal
            sourceFileId={sourceFile.id}
            methodSignature={gfc.methodSignature}
            fileName={sourceFile.fileName}
            onClose={() => setShowMethodModal(false)}
          />
        )}

        {showSourceModal && sourceFile && (
          <SourceFileViewerModal
            sourceFile={sourceFile}
            methods={methods}
            existingGfcs={projectGfcs}
            generatingSignature={generatingSignature}
            onClose={() => setShowSourceModal(false)}
            onGenerate={async (method) => {
              await handleSelectMethod(method);
              setShowSourceModal(false);
            }}
            onOpenGfc={(id) => {
              if (id === gfc.id) {
                setShowSourceModal(false);
                return;
              }
              setShowSourceModal(false);
              navigate(`/projeto/${projectId}/gfc/${id}`);
            }}
          />
        )}
      </div>
    </ReactFlowProvider>
  );
}
