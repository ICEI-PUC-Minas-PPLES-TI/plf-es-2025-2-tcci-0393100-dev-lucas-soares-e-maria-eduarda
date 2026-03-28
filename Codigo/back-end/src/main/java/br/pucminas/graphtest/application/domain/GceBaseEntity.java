package br.pucminas.graphtest.application.domain;

/**
 * Entidade base do dominio do GCE.
 *
 * <p>As entidades relacionadas ao grafo utilizam o mesmo identificador do
 * Neo4j, representado por um {@link Long}.</p>
 */
public abstract class GceBaseEntity {

    protected Long id;

    /**
     * Retorna o identificador persistido da entidade.
     *
     * @return identificador da entidade no Neo4j
     */
    public Long getId() {
        return id;
    }
}
