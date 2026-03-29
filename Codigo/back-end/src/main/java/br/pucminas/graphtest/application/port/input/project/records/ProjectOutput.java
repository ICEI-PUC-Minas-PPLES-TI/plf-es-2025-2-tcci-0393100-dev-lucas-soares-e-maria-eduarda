package br.pucminas.graphtest.application.port.input.project.records;

import br.pucminas.graphtest.application.domain.Project;

import java.util.UUID;

public record ProjectOutput(
    UUID id,
    String name,
    String description,
    UUID idUser
) {
    public static ProjectOutput from(Project project) {
        return new ProjectOutput(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getUserId()
        );
    }
}
