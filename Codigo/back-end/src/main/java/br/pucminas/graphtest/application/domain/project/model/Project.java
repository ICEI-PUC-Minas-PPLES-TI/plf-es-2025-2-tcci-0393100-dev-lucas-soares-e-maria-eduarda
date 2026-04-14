package br.pucminas.graphtest.application.domain.project.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Project extends BaseEntity {

    private String name;
    private String description;
    private UUID userId;

    public Project() {
    }

    public Project(UUID id, String name, String description, UUID userId) {
        this(id, name, description, userId, null, null);
    }

    public Project(UUID id,
                   String name,
                   String description,
                   UUID userId,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.description = description;
        this.userId = userId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getUserId() {
        return userId;
    }

}
