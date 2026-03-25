package br.pucminas.graphtest.application.port.output.repositories;

import br.pucminas.graphtest.application.domain.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findAll();
    List<Project> findAllByUserId(UUID userId);
    Optional<Project> findByIdAndUserId(UUID id, UUID userId);
    void deleteById(UUID id);
    long countByUserId(UUID userId);
}
