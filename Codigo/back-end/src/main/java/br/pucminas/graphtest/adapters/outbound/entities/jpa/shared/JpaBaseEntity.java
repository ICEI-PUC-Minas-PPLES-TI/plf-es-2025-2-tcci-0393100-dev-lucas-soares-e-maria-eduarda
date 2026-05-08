package br.pucminas.graphtest.adapters.outbound.entities.jpa.shared;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Classe base para entidades JPA cujo identificador e gerado pelo provedor de persistencia.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class JpaBaseEntity extends JpaAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    protected UUID id;
}
