package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;

import java.util.List;

/**
 * DTO HTTP para adicionar um novo no a um GCE existente.
 */
public record AddGceNodeDTO(
        String code,
        String label,
        GceNodeTypeEnum type,
        GceOperatorTypeEnum operatorType,
        List<String> sourceNodeCodes,
        List<String> targetNodeCodes
) {
}
