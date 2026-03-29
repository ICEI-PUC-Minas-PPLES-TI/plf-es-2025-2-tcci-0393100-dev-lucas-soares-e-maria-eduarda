package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

import java.util.List;

/**
 * Dados de entrada de um no do GCE.
 */
public record GceNodeInput(
        String code,
        String label,
        GceNodeTypeEnum type,
        GceOperatorTypeEnum operatorType,
        List<String> sourceNodeCodes,
        List<String> targetNodeCodes
) {

    public GceNodeInput(
            String code,
            String label,
            GceNodeTypeEnum type,
            GceOperatorTypeEnum operatorType
    ) {
        this(code, label, type, operatorType, List.of(), List.of());
    }
}
