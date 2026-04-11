package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.port.input.gce.PatchGceDetailsUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.UpdateGceDetailsInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

public class PatchGceDetailsUseCaseImpl implements PatchGceDetailsUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;
    private final GceMutationService gceMutationService;

    public PatchGceDetailsUseCaseImpl(GceRepositoryPort gceRepository,
                                      ProjectAccessService projectAccessService,
                                      GceMutationService gceMutationService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
        this.gceMutationService = gceMutationService;
    }

    @Override
    public GceOutput execute(UpdateGceDetailsInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.id(), gceRepository, projectAccessService);
        graph.updateDetails(input.name(), input.description());
        graph.markUpdatedNow();
        return GceOutput.from(gceRepository.save(graph));
    }
}
