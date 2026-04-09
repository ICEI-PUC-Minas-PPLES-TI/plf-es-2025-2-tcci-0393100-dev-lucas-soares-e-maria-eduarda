package br.pucminas.graphtest.application.service.project.interfaces;

import br.pucminas.graphtest.application.domain.project.model.Project;

import java.util.UUID;

public interface ProjectDeletionService {
    void deleteProject(Project project);
    void deleteProjectsByUserId(UUID userId);
}
