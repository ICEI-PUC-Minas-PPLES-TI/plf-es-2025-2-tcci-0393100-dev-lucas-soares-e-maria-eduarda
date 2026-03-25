package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.UpdateProjectUseCase;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.project.records.UpdateProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.springframework.stereotype.Service;

@Service
public class UpdateProjectUseCaseImpl implements UpdateProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    public UpdateProjectUseCaseImpl(ProjectRepository projectRepository,
                                    ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
    }

    @Override
    public ProjectOutput execute(UpdateProjectInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());

        project.setName(input.name());
        project.setDescription(input.description());

        return ProjectOutput.from(projectRepository.save(project));
    }



}
