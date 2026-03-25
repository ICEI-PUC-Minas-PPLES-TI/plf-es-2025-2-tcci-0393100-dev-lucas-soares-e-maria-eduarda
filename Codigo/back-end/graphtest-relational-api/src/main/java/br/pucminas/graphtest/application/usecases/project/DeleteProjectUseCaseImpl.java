package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.DeleteProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.springframework.stereotype.Service;

@Service
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    public DeleteProjectUseCaseImpl(ProjectRepository projectRepository,
                                    ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public void execute(DeleteProjectInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());
        projectRepository.deleteById(project.getId());
    }
}
