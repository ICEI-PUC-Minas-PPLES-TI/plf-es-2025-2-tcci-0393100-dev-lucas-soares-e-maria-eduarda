package br.pucminas.graphtest.application.domain.enums;

import br.pucminas.graphtest.application.exception.InvalidNodeException;

public enum GceNodeTypeEnum {

    CAUSE,
    EFFECT,
    OPERATOR;

    public static GceNodeTypeEnum getNodeType(String codigo) {
        if (codigo == null) {
            return null;
        }

        try {
            return GceNodeTypeEnum.valueOf(codigo);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static GceNodeTypeEnum getNodeTypeOrThrow(String codigo) {
        GceNodeTypeEnum node = getNodeType(codigo);
        if (node == null) {
            throw new InvalidNodeException("Tipo de no invalido: " + codigo);
        }
        return node;
    }
}
