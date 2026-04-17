package br.pucminas.graphtest.adapters.outbound.entities.jpa.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Classe base para entidades JPA cujo UUID e atribuido pela aplicacao antes da persistencia.
 *
 * <p>Ela existe para agregados que precisam montar referencias internas em memoria antes de tocar
 * o banco, como a tabela de decisao.</p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract class JpaAssignedIdEntity extends JpaAuditableEntity {

    @Id
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    protected UUID id;
}
