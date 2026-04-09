package br.pucminas.graphtest.application.domain.gce.enums;

import br.pucminas.graphtest.application.exception.InvalidEdgeTypeException;

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

    /**
     * Tenta resolver um tipo de aresta a partir de seu codigo textual.
     *
     * @param codigo codigo recebido
     * @return tipo correspondente ou {@code null} quando invalido
     */
    public static GceEdgeTypeEnum getEdgeType(String codigo) {
        if (codigo == null) {
            return null;
        }

        try {
            return GceEdgeTypeEnum.valueOf(codigo);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Resolve um tipo de aresta ou lanca excecao quando o codigo e invalido.
     *
     * @param codigo codigo recebido
     * @return tipo correspondente
     */
    public static GceEdgeTypeEnum getEdgeTypeOrThrow(String codigo) {
        GceEdgeTypeEnum edgeType = getEdgeType(codigo);
        if (edgeType == null) {
            throw new InvalidEdgeTypeException("Tipo de aresta invalido: " + codigo);
        }
        return edgeType;
    }
}
