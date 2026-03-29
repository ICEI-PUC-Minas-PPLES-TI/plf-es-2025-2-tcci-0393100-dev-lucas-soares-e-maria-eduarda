package br.pucminas.graphtest.application.domain.enums;

import br.pucminas.graphtest.application.exception.InvalidOperatorException;

/**
 * Enumera os operadores logicos suportados por nos operadores do GCE.
 */
public enum GceOperatorTypeEnum {

    AND,
    OR;

    /**
     * Tenta resolver um operador logico a partir de seu codigo textual.
     *
     * @param codigo codigo recebido
     * @return operador correspondente ou {@code null} quando invalido
     */
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

    /**
     * Resolve um operador logico ou lanca excecao quando o codigo e invalido.
     *
     * @param codigo codigo recebido
     * @return operador correspondente
     */
    public static GceOperatorTypeEnum getOperatorTypeOrThrow(String codigo) {
        GceOperatorTypeEnum operator = getOperatorType(codigo);
        if (operator == null) {
            throw new InvalidOperatorException("Tipo de operador invalido: " + codigo);
        }
        return operator;
    }
}
