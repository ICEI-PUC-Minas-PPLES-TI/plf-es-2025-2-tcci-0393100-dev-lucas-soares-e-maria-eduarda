package br.pucminas.graphtest.application.service.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;

import java.util.List;
import java.util.UUID;

public class ProjectDeletionServiceImpl implements ProjectDeletionService {

    private final ProjectRepositoryPort projectRepository;
    private final GceRepositoryPort gceRepository;
    private final DecisionTableRepositoryPort decisionTableRepository;

    public ProjectDeletionServiceImpl(ProjectRepositoryPort projectRepository,
                                      GceRepositoryPort gceRepository,
                                      DecisionTableRepositoryPort decisionTableRepository) {
        this.projectRepository = projectRepository;
        this.gceRepository = gceRepository;
        this.decisionTableRepository = decisionTableRepository;
    }

    @Override
    public void deleteProject(Project project) {
        gceRepository.deleteAllByProjectId(project.getId());
        decisionTableRepository.deleteAllByProjectId(project.getId());
        projectRepository.deleteById(project.getId());
    }

    @Override
    public void deleteProjectsByUserId(UUID userId) {
        List<Project> projects = projectRepository.findAllByUserId(userId);

        for (Project project : projects) {
            gceRepository.deleteAllByProjectId(project.getId());
            decisionTableRepository.deleteAllByProjectId(project.getId());
        }

        projectRepository.deleteAllByUserId(userId);
    }
}
