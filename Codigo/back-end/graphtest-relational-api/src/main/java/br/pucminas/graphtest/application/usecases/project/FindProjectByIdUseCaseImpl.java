package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.FindProjectByIdUseCase;
import br.pucminas.graphtest.application.port.input.project.records.FindProjectByIdInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.springframework.stereotype.Service;


@Service
public class FindProjectByIdUseCaseImpl implements FindProjectByIdUseCase {

    private final ProjectAccessService projectAccessService;

    public FindProjectByIdUseCaseImpl(ProjectAccessService projectAccessService) {
        this.projectAccessService = projectAccessService;
    }

    @Override
    public ProjectOutput execute(FindProjectByIdInput input) {
        Project project = projectAccessService.findAuthorizedProject(input.id());
        return ProjectOutput.from(project);
    }
}
