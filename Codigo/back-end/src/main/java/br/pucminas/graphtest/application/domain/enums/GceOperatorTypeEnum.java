package br.pucminas.graphtest.application.domain.enums;

import br.pucminas.graphtest.application.exception.InvalidOperatorException;

public enum GceOperatorTypeEnum {

    AND,
    OR;

    public static GceOperatorTypeEnum getOperatorType(String codigo) {
        if (codigo == null) {
            return null;
        }

        try {
            return GceOperatorTypeEnum.valueOf(codigo);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static GceOperatorTypeEnum getOperatorTypeOrThrow(String codigo) {
        GceOperatorTypeEnum operator = getOperatorType(codigo);
        if (operator == null) {
            throw new InvalidOperatorException("Tipo de operador invalido: " + codigo);
        }
        return operator;
    }
}
