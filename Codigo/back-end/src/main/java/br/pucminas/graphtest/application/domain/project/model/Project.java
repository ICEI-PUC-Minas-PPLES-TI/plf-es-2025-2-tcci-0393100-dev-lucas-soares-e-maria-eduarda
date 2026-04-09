package br.pucminas.graphtest.application.domain.project.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.util.UUID;

public class Project extends BaseEntity {

    private String name;
    private String description;
    private UUID userId;

    public Project() {
    }

    public Project(UUID id, String name, String description, UUID userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
    }


    public void setId(UUID id) {
        this.id = id;
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
