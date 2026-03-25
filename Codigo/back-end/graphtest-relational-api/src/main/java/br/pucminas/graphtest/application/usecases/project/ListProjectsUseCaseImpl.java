package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.project.ListProjectsUseCase;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListProjectsUseCaseImpl implements ListProjectsUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserPort currentUserPort;

    public ListProjectsUseCaseImpl(ProjectRepository projectRepository,
                                   CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public List<ProjectOutput> execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new UnauthorizedUserException("Somente administradores podem listar todos os projetos");
        }

        return projectRepository.findAll()
                .stream()
                .map(ProjectOutput::from)
                .toList();
    }
}
