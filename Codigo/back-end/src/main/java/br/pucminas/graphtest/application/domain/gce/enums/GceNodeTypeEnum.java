package br.pucminas.graphtest.application.domain.gce.enums;

import br.pucminas.graphtest.application.exception.InvalidNodeException;

/**
 * Enumera os tipos de no presentes em um Grafo de Causa e Efeito.
 */
public enum GceNodeTypeEnum {

    CAUSE,
    EFFECT,
    OPERATOR;

    /**
     * Tenta resolver um tipo de no a partir de seu codigo textual.
     *
     * @param codigo codigo recebido
     * @return tipo correspondente ou {@code null} quando invalido
     */
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

    /**
     * Resolve um tipo de no ou lanca excecao quando o codigo e invalido.
     *
     * @param codigo codigo recebido
     * @return tipo correspondente
     */
    public static GceNodeTypeEnum getNodeTypeOrThrow(String codigo) {
        GceNodeTypeEnum node = getNodeType(codigo);
        if (node == null) {
            throw new InvalidNodeException("Tipo de no invalido: " + codigo);
        }
        return node;
    }
}
