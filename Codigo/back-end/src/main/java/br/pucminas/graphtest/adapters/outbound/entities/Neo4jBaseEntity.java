package br.pucminas.graphtest.adapters.outbound.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

/**
 * Classe base abstrata para entidades persistidas no Neo4j.
 */
@Data
public abstract class Neo4jBaseEntity {

    /**
     * Identificador unico da entidade.
     */
    @Id
    @GeneratedValue
    protected Long id;
}
