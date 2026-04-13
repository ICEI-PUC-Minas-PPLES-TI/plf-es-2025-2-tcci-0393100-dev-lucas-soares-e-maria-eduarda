package br.pucminas.graphtest.application.domain.shared.model;

import java.util.UUID;

public abstract class BaseEntity {

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
