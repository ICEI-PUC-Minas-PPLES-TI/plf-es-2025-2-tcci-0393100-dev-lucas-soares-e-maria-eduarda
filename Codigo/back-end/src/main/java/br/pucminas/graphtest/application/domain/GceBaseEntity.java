package br.pucminas.graphtest.application.domain;

import java.util.UUID;

/**
 * Entidade base do dominio do GCE.
 *
 * <p>As entidades relacionadas ao grafo utilizam identificadores externos
 * gerados pela aplicacao, independentes do id interno do Neo4j.</p>
 */
public abstract class GceBaseEntity {

    protected UUID id;

    /**
     * Retorna o identificador externo da entidade.
     *
     * @return identificador externo da entidade
     */
    public UUID getId() {
        return id;
    }
}
