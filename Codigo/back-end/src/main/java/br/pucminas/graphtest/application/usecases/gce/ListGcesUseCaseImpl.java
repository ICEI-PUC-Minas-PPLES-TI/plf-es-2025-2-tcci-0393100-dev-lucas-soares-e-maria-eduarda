package br.pucminas.graphtest.application.usecases.gce;

import br.pucminas.graphtest.application.port.input.gce.ListGcesUseCasePort;
import br.pucminas.graphtest.application.port.input.gce.records.GceOutput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;

import java.util.List;

public class ListGcesUseCaseImpl implements ListGcesUseCasePort {

    private final GceRepositoryPort gceRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;

    public ListGcesUseCaseImpl(GceRepositoryPort gceRepository,
                               ProjectRepositoryPort projectRepository,
                               CurrentUserPort currentUserPort) {
        this.gceRepository = gceRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public List<GceOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        return projectRepository.findAllByUserId(currentUser.id()).stream()
                .flatMap(project -> gceRepository.findAllByProjectId(project.getId()).stream())
                .map(GceOutput::from)
                .toList();
    }
}
