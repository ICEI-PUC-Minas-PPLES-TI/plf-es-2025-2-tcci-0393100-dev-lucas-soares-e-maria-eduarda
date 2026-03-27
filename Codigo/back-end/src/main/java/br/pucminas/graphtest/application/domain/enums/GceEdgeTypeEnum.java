package br.pucminas.graphtest.application.domain.enums;

import br.pucminas.graphtest.application.exception.InvalidEdgeTypeException;

public enum GceEdgeTypeEnum {

    IDENTITY,
    NEGATED;

    public boolean apply(boolean sourceValue) {
        return switch (this) {
            case IDENTITY -> sourceValue;
            case NEGATED -> !sourceValue;
        };
    }

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

    public static GceEdgeTypeEnum getEdgeTypeOrThrow(String codigo) {
        GceEdgeTypeEnum edgeType = getEdgeType(codigo);
        if (edgeType == null) {
            throw new InvalidEdgeTypeException("Tipo de aresta invalido: " + codigo);
        }
        return edgeType;
    }
}
