package br.pucminas.graphtest.adapters.outbound.entities.jpa;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Classe base abstrata para todas as entidades persistentes do sistema
 *
 * @author lucas S.
 * @since 1.0
 */
@MappedSuperclass
@Data
public abstract class JpaBaseEntity implements Serializable {

    /**
     * Identificador de versão utilizado no processo de serialização da classe.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único da entidade
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    protected UUID id;

}
