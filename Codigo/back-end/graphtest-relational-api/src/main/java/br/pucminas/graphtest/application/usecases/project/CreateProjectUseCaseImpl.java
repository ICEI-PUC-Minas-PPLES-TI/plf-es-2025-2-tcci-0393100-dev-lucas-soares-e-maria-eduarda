package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.port.input.project.CreateProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.records.CreateProjectInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class CreateProjectUseCaseImpl implements CreateProjectUseCase {

    private static final String DEFAULT_PROJECT_NAME_PATTERN = "Projeto %d";

    private final ProjectRepository projectRepository;
    private final CurrentUserPort currentUserPort;

    public CreateProjectUseCaseImpl(ProjectRepository projectRepository, CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public ProjectOutput execute(CreateProjectInput input) {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        Project project = new Project(
                null,
                input.name(),
                input.description(),
                currentUser.id()
        );

        if (project.getName() == null || project.getName().isBlank()) {
            project.setName(generateDefaultProjectName(currentUser.id()));
        }


        return ProjectOutput.from(projectRepository.save(project));
    }

    private String generateDefaultProjectName(UUID userId) {
        long count = projectRepository.countByUserId(userId) + 1;
        return String.format(DEFAULT_PROJECT_NAME_PATTERN, count);
    }
}
