package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.gce.AddNodeToGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.AddNodeToGceInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceNodeInput;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;

/**
 * Caso de uso responsavel por adicionar um no a um GCE existente.
 */
public class AddNodeToGceUseCaseImpl implements AddNodeToGceUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;
    private final DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService;

    public AddNodeToGceUseCaseImpl(GceRepositoryPort gceRepository,
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
    public GceOutput execute(AddNodeToGceInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.gceId(), gceRepository, projectAccessService);
        gceMutationService.addNodeWithAutomaticEdges(
                graph,
                new GceNodeInput(
                        input.code(),
                        input.label(),
                        input.type(),
                        input.operatorType(),
                        input.sourceNodeCodes(),
                        input.targetNodeCodes()
                )
        );

        gceMutationService.refreshOperatorLabels(graph);
        gceMutationService.validateAndThrow(graph, gceValidationResultService);
        graph.setUpdatedAt(LocalDateTime.now());
        Gce persistedGraph = gceRepository.save(graph);
        decisionTableSyncStatusUpdateService.markDecisionTableAsStaleByGceId(persistedGraph.getId());
        return GceOutput.from(persistedGraph);
    }
}
