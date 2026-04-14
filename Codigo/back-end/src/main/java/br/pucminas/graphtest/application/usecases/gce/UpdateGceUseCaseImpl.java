package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.gce.UpdateGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceValidationResultService;
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

    public UpdateGceUseCaseImpl(GceRepositoryPort gceRepository,
                                ProjectAccessService projectAccessService,
                                GceValidationResultService gceValidationResultService,
                                GceMutationService gceMutationService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceValidationResultService = gceValidationResultService;
        this.gceMutationService = gceMutationService;
    }

    @Override
    public GceOutput execute(UpdateGceInput input) {
        Gce currentGraph = gceMutationService.loadAuthorizedGraph(input.id(), gceRepository, projectAccessService);

        if (!currentGraph.getProjectId().equals(input.projectId())) {
            throw new IllegalArgumentException("Nao e permitido alterar o projectId do GCE.");
        }

        Gce updatedGraph = new Gce(
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

        gceMutationService.refreshOperatorLabels(updatedGraph);
        gceMutationService.validateAndThrow(updatedGraph, gceValidationResultService);
        return GceOutput.from(gceRepository.save(updatedGraph));
    }
}
