package br.pucminas.graphtest.application.port.input.project.records;

import br.pucminas.graphtest.application.domain.project.model.Project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectOutput(
    UUID id,
    String name,
    String description,
    UUID idUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProjectOutput from(Project project) {
        return new ProjectOutput(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getUserId(),
            project.getCreatedAt(),
            normalizeUpdatedAt(project.getCreatedAt(), project.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
