package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectAccessServiceImpl implements ProjectAccessService {

    private static final String PROJECT_NOT_FOUND = "Projeto não encontrado";

    private final ProjectRepository projectRepository;
    private final CurrentUserPort currentUserPort;

    public ProjectAccessServiceImpl(ProjectRepository projectRepository,
                                    CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }


    @Override
    public Project findAuthorizedProject(UUID projectId) {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (currentUser.isAdmin()) {
            return projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND));
        }

        return projectRepository.findByIdAndUserId(projectId, currentUser.id())
                .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND));
    }
}