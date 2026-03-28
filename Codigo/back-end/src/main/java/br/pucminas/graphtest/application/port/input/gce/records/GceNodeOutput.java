package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

/**
 * Saida de um no do GCE.
 */
public record GceNodeOutput(
        Long id,
        String code,
        String label,
        GceNodeTypeEnum type,
        GceOperatorTypeEnum operatorType
) {

    /**
     * Converte um no de dominio em sua representacao de saida.
     *
     * @param node no do dominio
     * @return saida correspondente
     */
    public static GceNodeOutput from(GceNode node) {
        return new GceNodeOutput(
                node.getId(),
                node.getCode(),
                node.getLabel(),
                node.getType(),
                node.getOperatorType()
        );
    }
}
