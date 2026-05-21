package br.pucminas.graphtest.application.port.input.gfc.records;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;

import java.util.UUID;

/**
 * Saida de um no do GFC.
 */
public record GfcNodeOutput(
        UUID id,
        String code,
        String label,
        GfcNodeTypeEnum type,
        Integer startLine,
        Integer endLine
) {

    /**
     * Converte um no de dominio em sua representacao de saida.
     *
     * @param node no do dominio
     * @return saida correspondente
     */
    public static GfcNodeOutput from(GfcNode node) {
        return new GfcNodeOutput(
                node.getId(),
                node.getCode(),
                node.getLabel(),
                node.getType(),
                node.getStartLine(),
                node.getEndLine()
        );
    }
}
