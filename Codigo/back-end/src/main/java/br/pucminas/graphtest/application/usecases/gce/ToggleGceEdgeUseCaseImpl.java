package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.ToggleGceEdgeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ToggleGceEdgeInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;

/**
 * Caso de uso responsavel por inverter o tipo de uma aresta existente do GCE.
 */
public class ToggleGceEdgeUseCaseImpl implements ToggleGceEdgeUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;
    private final DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService;

    public ToggleGceEdgeUseCaseImpl(GceRepositoryPort gceRepository,
                                    ProjectAccessService projectAccessService,
                                    GceValidationResultService gceValidationResultService,
                                    GceMutationService gceMutationService,
                                    DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceValidationResultService = gceValidationResultService;
        this.gceMutationService = gceMutationService;
        this.decisionTableSyncStatusUpdateService = decisionTableSyncStatusUpdateService;
    }

    @Override
    public GceOutput execute(ToggleGceEdgeInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        if (!graph.getProjectId().equals(input.projectId())) {
            throw new EntityNotFoundException("GCE nao encontrado");
        }
        GceEdge currentEdge = graph.findEdge(input.edgeId())
                .orElseThrow(() -> new IllegalArgumentException("Aresta inexistente: " + input.edgeId()));

        GceEdgeTypeEnum toggledType = currentEdge.getType() == GceEdgeTypeEnum.IDENTITY
                ? GceEdgeTypeEnum.NEGATED
                : GceEdgeTypeEnum.IDENTITY;

        GceEdge updatedEdge = new GceEdge(
                currentEdge.getId(),
                currentEdge.getSourceNodeCode(),
                currentEdge.getTargetNodeCode(),
                toggledType,
                currentEdge.getCreatedAt(),
                LocalDateTime.now()
        );

        graph.replaceEdge(updatedEdge);

        gceMutationService.validateAndThrow(graph, gceValidationResultService);
        graph.setUpdatedAt(LocalDateTime.now());
        Gce persistedGraph = gceRepository.save(graph);
        decisionTableSyncStatusUpdateService.markDecisionTableAsStaleByGceId(persistedGraph.getId());
        return GceOutput.from(persistedGraph);
    }
}
