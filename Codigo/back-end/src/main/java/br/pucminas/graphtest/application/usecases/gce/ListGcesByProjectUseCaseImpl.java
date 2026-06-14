package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.port.input.gce.ListGcesByProjectUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.input.gce.records.ListGcesByProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;

import java.util.List;

public class ListGcesByProjectUseCaseImpl implements ListGcesByProjectUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectAccessService projectAccessService;

    public ListGcesByProjectUseCaseImpl(GceRepositoryPort gceRepository,
                                        ProjectAccessService projectAccessService) {
        this.gceRepository = gceRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public List<GceOutput> execute(ListGcesByProjectInput input) {
        projectAccessService.findAuthorizedProject(input.projectId());

        return gceRepository.findAllByProjectId(input.projectId()).stream()
                .map(GceOutput::from)
                .toList();
    }
}
