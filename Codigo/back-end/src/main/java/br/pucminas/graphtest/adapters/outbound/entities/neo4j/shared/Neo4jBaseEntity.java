package br.pucminas.graphtest.adapters.outbound.entities.neo4j.shared;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe base abstrata para entidades persistidas no Neo4j.
 */
@Data
public abstract class Neo4jBaseEntity {

    /**
     * Identificador externo unico da entidade.
     */
    @Id
    @GeneratedValue
    protected UUID id;

    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;
}
