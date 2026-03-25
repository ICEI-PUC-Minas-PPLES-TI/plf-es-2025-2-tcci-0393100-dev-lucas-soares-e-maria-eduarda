package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.project.ListProjectsByUserUseCase;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProjectsByUserUseCaseImpl implements ListProjectsByUserUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserPort currentUserPort;

    public ListProjectsByUserUseCaseImpl(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public List<ProjectOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        return projectRepository.findAllByUserId(currentUser.id()).stream()
                .map(ProjectOutput::from)
                .toList();
    }
}
