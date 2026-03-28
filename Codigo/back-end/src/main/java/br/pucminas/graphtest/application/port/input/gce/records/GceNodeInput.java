package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

/**
 * Dados de entrada de um no do GCE.
 */
public record GceNodeInput(
        String code,
        String label,
        GceNodeTypeEnum type,
        GceOperatorTypeEnum operatorType
) {
}
