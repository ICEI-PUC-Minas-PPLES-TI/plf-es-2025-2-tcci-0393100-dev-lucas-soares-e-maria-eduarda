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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void restoreAuditFields(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markCreatedNow() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void markUpdatedNow() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }
}
