import { useState, useCallback, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { ReactFlowProvider } from '@xyflow/react';
import { Header } from '../components/Header';
import { GCEToolbar } from '../features/gce/components/GCEToolbar';
import { ElementPalette } from '../features/gce/components/ElementPalette';
import { GCECanvas, type GCECanvasHandle } from '../features/gce/components/GCECanvas';
import { PropertiesPanel } from '../features/gce/components/PropertiesPanel';
import { ValidationPanel } from '../features/gce/components/ValidationPanel';
import { GCE_MOCK, dtoToFlowNodes, dtoToFlowEdges, dtoToRestrictions } from '../features/gce/mocks/gceMock';
import type { GCEValidationError } from '../features/gce/types/gce';

export function GCEEditorPage() {
  const { projectId } = useParams<{ projectId: string; gceId: string }>();

  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [selectedEdgeId, setSelectedEdgeId] = useState<string | null>(null);
  const [showValidation, setShowValidation] = useState(false);
  const [validationErrors, setValidationErrors] = useState<GCEValidationError[]>([]);

  const handleSelectionChange = useCallback((nodeId: string | null, edgeId: string | null) => {
    setSelectedNodeId(nodeId);
    setSelectedEdgeId(edgeId);
  }, []);

  const handleValidate = () => {
    setValidationErrors([]);
    setShowValidation(true);
  };

  const handleSave = () => {
    // TODO: integrate with backend
  };

  const handleGenerateTable = () => {
    // TODO: navigate to table generation
  };

  const handleSelectError = (elementId: string) => {
    setSelectedNodeId(elementId);
    setSelectedEdgeId(null);
  };

  return (
    <ReactFlowProvider>
      <div className="h-screen flex flex-col bg-surface">
        <Header
          breadcrumb={[
            { label: 'Projetos', href: '/homepage' },
            { label: 'Projeto', href: `/projeto/${projectId}` },
            { label: GCE_MOCK.name },
          ]}
        />

        <GCEToolbar
          gceName={GCE_MOCK.name}
          onSave={handleSave}
          onValidate={handleValidate}
          onGenerateTable={handleGenerateTable}
        />

        <GCEEditorContent
          selectedNodeId={selectedNodeId}
          selectedEdgeId={selectedEdgeId}
          showValidation={showValidation}
          validationErrors={validationErrors}
          onSelectionChange={handleSelectionChange}
          onCloseValidation={() => setShowValidation(false)}
          onSelectError={handleSelectError}
        />
      </div>
    </ReactFlowProvider>
  );
}

function GCEEditorContent({
  selectedNodeId,
  selectedEdgeId,
  showValidation,
  validationErrors,
  onSelectionChange,
  onCloseValidation,
  onSelectError,
}: {
  selectedNodeId: string | null;
  selectedEdgeId: string | null;
  showValidation: boolean;
  validationErrors: GCEValidationError[];
  onSelectionChange: (nodeId: string | null, edgeId: string | null) => void;
  onCloseValidation: () => void;
  onSelectError: (elementId: string) => void;
}) {
  const canvasRef = useRef<GCECanvasHandle>(null);

  return (
    <div className="flex-1 flex overflow-hidden">
      <ElementPalette
        onAddNode={(type, op) => canvasRef.current?.addNode(type, op)}
        onDelete={() => canvasRef.current?.deleteSelected()}
        hasSelection={canvasRef.current?.hasSelection ?? false}
      />

      <div className="flex-1 flex flex-col relative">
        <GCECanvas
          ref={canvasRef}
          initialNodes={dtoToFlowNodes(GCE_MOCK)}
          initialEdges={dtoToFlowEdges(GCE_MOCK)}
          initialRestrictions={dtoToRestrictions(GCE_MOCK)}
          onSelectionChange={onSelectionChange}
        />

        {showValidation && (
          <ValidationPanel
            errors={validationErrors}
            onClose={onCloseValidation}
            onSelectError={onSelectError}
          />
        )}
      </div>

      <PropertiesPanel
        selectedNodeId={selectedNodeId}
        selectedEdgeId={selectedEdgeId}
      />
    </div>
  );
}
