package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.gce.DeleteGceUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.DeleteGceInput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

public class DeleteGceUseCaseImpl implements DeleteGceUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final DecisionTableRepositoryPort decisionTableRepository;
    private final ProjectAccessService projectAccessService;
    private final GceMutationService gceMutationService;

    public DeleteGceUseCaseImpl(GceRepositoryPort gceRepository,
                                DecisionTableRepositoryPort decisionTableRepository,
                                ProjectAccessService projectAccessService,
                                GceMutationService gceMutationService) {
        this.gceRepository = gceRepository;
        this.decisionTableRepository = decisionTableRepository;
        this.projectAccessService = projectAccessService;
        this.gceMutationService = gceMutationService;
    }

    @Override
    public void execute(DeleteGceInput input) {
        Gce graph = gceMutationService.loadAuthorizedGraph(input.id(), gceRepository, projectAccessService);
        ensureGraphBelongsToProject(graph, input.projectId());
        decisionTableRepository.findByGceId(graph.getId()).ifPresent(decisionTable -> {
            decisionTable.clearGceReference();
            decisionTableRepository.save(decisionTable);
        });
        gceRepository.deleteById(graph.getId());
    }

    private void ensureGraphBelongsToProject(Gce graph, java.util.UUID projectId) {
        if (!graph.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("GCE nao encontrado");
        }
    }
}
