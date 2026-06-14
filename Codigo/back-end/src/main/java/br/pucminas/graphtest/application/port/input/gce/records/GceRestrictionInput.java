package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;

import java.util.List;

/**
 * Dados de entrada de uma restricao do GCE.
 */
public record GceRestrictionInput(
        RestrictionTypeEnum type,
        List<String> nodeCodes
) {
}
