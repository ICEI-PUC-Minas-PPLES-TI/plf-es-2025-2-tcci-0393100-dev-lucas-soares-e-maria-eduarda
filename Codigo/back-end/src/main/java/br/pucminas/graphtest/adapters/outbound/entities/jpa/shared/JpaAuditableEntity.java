package br.pucminas.graphtest.adapters.outbound.entities.jpa.shared;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe base com metadados de auditoria compartilhados por entidades JPA do sistema.
 *
 * <p>A estrategia de identificacao fica nas subclasses concretas. Isso permite reaproveitar
 * {@code createdAt}/{@code updatedAt} sem misturar entidades com ID gerado pelo JPA e entidades
 * com ID atribuido pela aplicacao.</p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract class JpaAuditableEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
