package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;

import java.util.UUID;

/**
 * Dados para atualizar um no existente do GCE.
 */
public record UpdateGceNodeInput(
        UUID projectId,
        UUID gceId,
        String nodeCode,
        String label,
        GceOperatorTypeEnum operatorType
) {
}
