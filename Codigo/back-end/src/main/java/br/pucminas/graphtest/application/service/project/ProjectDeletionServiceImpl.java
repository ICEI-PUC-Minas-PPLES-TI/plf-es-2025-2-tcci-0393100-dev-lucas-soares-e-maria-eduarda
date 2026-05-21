package br.pucminas.graphtest.application.service.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;

import java.util.List;
import java.util.UUID;

public class ProjectDeletionServiceImpl implements ProjectDeletionService {

    private final ProjectRepositoryPort projectRepository;
    private final GceRepositoryPort gceRepository;
    private final DecisionTableRepositoryPort decisionTableRepository;
    private final GfcRepositoryPort gfcRepository;
    private final GfcSourceFileRepositoryPort gfcSourceFileRepository;

    public ProjectDeletionServiceImpl(ProjectRepositoryPort projectRepository,
                                      GceRepositoryPort gceRepository,
                                      DecisionTableRepositoryPort decisionTableRepository,
                                      GfcRepositoryPort gfcRepository,
                                      GfcSourceFileRepositoryPort gfcSourceFileRepository) {
        this.projectRepository = projectRepository;
        this.gceRepository = gceRepository;
        this.decisionTableRepository = decisionTableRepository;
        this.gfcRepository = gfcRepository;
        this.gfcSourceFileRepository = gfcSourceFileRepository;
    }

    @Override
    public void deleteProject(Project project) {
        gfcRepository.deleteAllByProjectId(project.getId());
        gfcSourceFileRepository.deleteAllByProjectId(project.getId());
        gceRepository.deleteAllByProjectId(project.getId());
        decisionTableRepository.deleteAllByProjectId(project.getId());
        projectRepository.deleteById(project.getId());
    }

    @Override
    public void deleteProjectsByUserId(UUID userId) {
        List<Project> projects = projectRepository.findAllByUserId(userId);

        for (Project project : projects) {
            gfcRepository.deleteAllByProjectId(project.getId());
            gfcSourceFileRepository.deleteAllByProjectId(project.getId());
            gceRepository.deleteAllByProjectId(project.getId());
            decisionTableRepository.deleteAllByProjectId(project.getId());
        }

        projectRepository.deleteAllByUserId(userId);
    }
}
