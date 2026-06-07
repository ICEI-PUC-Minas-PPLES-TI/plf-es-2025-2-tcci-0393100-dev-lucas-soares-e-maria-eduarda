package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableSyncStatusUpdateService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.time.LocalDateTime;

/**
 * Caso de uso responsavel por atualizar a representacao completa de um GCE.
 */
public class UpdateGceUseCaseImpl implements UpdateGceUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceValidationResultService gceValidationResultService;
    private final GceMutationService gceMutationService;
    private final DecisionTableSyncStatusUpdateService decisionTableSyncStatusUpdateService;

    public UpdateGceUseCaseImpl(GceRepositoryPort gceRepository,
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
    public GceOutput execute(UpdateGceInput input) {
        Gce currentGraph = gceMutationService.loadAuthorizedGraph(input.id(), gceRepository, projectAccessService);

        if (!currentGraph.getProjectId().equals(input.projectId())) {
            throw new EntityNotFoundException("GCE nao encontrado");
        }

        Gce updatedGraph = buildUpdatedGraph(currentGraph, input);
        validateUpdatedGraph(updatedGraph);
        boolean hasDecisionTableRelevantChanges = hasDecisionTableRelevantChanges(currentGraph, updatedGraph);
        Gce persistedGraph = persistGraph(updatedGraph);
        updateDecisionTableSyncStatusIfNecessary(persistedGraph, hasDecisionTableRelevantChanges);

        return GceOutput.from(persistedGraph);
    }


    private Gce buildUpdatedGraph(Gce currentGraph, UpdateGceInput input) {
        return new Gce(
                currentGraph.getId(),
                currentGraph.getProjectId(),
                input.name(),
                input.description(),
                Boolean.TRUE.equals(input.selected()),
                gceMutationService.toNodesForUpdate(currentGraph, input.nodes()),
                gceMutationService.toEdgesForUpdate(currentGraph, input.nodes(), input.edges()),
                gceMutationService.toRestrictionsForUpdate(currentGraph, input.restrictions()),
                currentGraph.getCreatedAt(),
                LocalDateTime.now()
        );
    }

    private void validateUpdatedGraph(Gce updatedGraph) {
        gceMutationService.refreshOperatorLabels(updatedGraph);
        gceMutationService.validateAndThrow(updatedGraph, gceValidationResultService);
    }

    private boolean hasDecisionTableRelevantChanges(Gce currentGraph, Gce updatedGraph) {
        return decisionTableSyncStatusUpdateService.hasDecisionTableRelevantChanges(currentGraph, updatedGraph);
    }

    private Gce persistGraph(Gce updatedGraph) {
        return gceRepository.save(updatedGraph);
    }

    private void updateDecisionTableSyncStatusIfNecessary(Gce persistedGraph, boolean hasDecisionTableRelevantChanges) {
        if (hasDecisionTableRelevantChanges) {
            decisionTableSyncStatusUpdateService.markDecisionTableAsStaleByGceId(persistedGraph.getId());
        }
    }
}
