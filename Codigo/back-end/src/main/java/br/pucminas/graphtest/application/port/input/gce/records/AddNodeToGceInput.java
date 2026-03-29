package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;

import java.util.List;
import java.util.UUID;

/**
 * Dados para adicionar um novo no ao GCE.
 */
public record AddNodeToGceInput(
        UUID gceId,
        String code,
        String label,
        GceNodeTypeEnum type,
        GceOperatorTypeEnum operatorType,
        List<String> sourceNodeCodes,
        List<String> targetNodeCodes
) {
}
