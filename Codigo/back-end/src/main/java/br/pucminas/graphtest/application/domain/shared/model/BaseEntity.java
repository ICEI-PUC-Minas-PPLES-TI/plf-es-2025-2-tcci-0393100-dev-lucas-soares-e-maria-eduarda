package br.pucminas.graphtest.application.domain.shared.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEntity {

    protected UUID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    /**
     * Retorna o identificador externo da entidade.
     *
     * @return identificador externo da entidade
     */
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
