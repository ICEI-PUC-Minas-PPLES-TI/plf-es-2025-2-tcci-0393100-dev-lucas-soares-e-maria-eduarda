package br.pucminas.graphtest.application.domain.gce.enums;

/**
 * Define os tipos de aresta suportados em um GCE.
 */
public enum GceEdgeTypeEnum {

    IDENTITY,
    NEGATED;

    /**
     * Aplica o comportamento logico da aresta ao valor de origem.
     *
     * @param sourceValue valor booleano produzido no no de origem
     * @return valor propagado ao no de destino
     */
    public boolean apply(boolean sourceValue) {
        return switch (this) {
            case IDENTITY -> sourceValue;
            case NEGATED -> !sourceValue;
        };
    }

}
