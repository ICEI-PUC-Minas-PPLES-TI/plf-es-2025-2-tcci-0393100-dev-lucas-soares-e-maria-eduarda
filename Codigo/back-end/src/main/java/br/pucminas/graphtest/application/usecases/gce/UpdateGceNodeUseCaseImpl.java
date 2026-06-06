package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceNodeUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceNodeInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;

/**
 * Caso de uso responsavel por atualizar um no existente do GCE.
 */
public class UpdateGceNodeUseCaseImpl implements UpdateGceNodeUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;
    private final DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService;

    public UpdateGceNodeUseCaseImpl(GceRepositoryPort gceRepository,
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
    public GceOutput execute(UpdateGceNodeInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        if (!graph.getProjectId().equals(input.projectId())) {
            throw new EntityNotFoundException("GCE nao encontrado");
        }
        GceNode currentNode = graph.findNode(input.nodeCode())
                .orElseThrow(() -> new IllegalArgumentException("No inexistente: " + input.nodeCode()));

        GceNode updatedNode = new GceNode(
                currentNode.getId(),
                currentNode.getCode(),
                currentNode.isOperator()
                        ? currentNode.getCode()
                        : input.label() != null && !input.label().isBlank() ? input.label() : currentNode.getLabel(),
                currentNode.getType(),
                currentNode.isOperator() && input.operatorType() != null
                        ? input.operatorType()
                        : currentNode.getOperatorType(),
                currentNode.getCreatedAt(),
                LocalDateTime.now()
        );

        graph.replaceNode(updatedNode);
        gceMutationService.refreshOperatorLabels(graph);
        gceMutationService.validateAndThrow(graph, gceValidationResultService);
        graph.setUpdatedAt(LocalDateTime.now());
        Gce persistedGraph = gceRepository.save(graph);
        decisionTableSyncStatusUpdateService.markDecisionTableAsStaleByGceId(persistedGraph.getId());
        return GceOutput.from(persistedGraph);
    }
}
