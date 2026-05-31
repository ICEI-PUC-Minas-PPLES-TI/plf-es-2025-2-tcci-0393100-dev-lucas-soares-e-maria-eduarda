package br.pucminas.graphtest.application.port.input.project;

import br.pucminas.graphtest.application.port.input.project.records.ProjectArtifactOutput;

import java.util.List;
import java.util.UUID;

public interface ListProjectArtifactsUseCasePort {

    List<ProjectArtifactOutput> execute(UUID projectId);
}
