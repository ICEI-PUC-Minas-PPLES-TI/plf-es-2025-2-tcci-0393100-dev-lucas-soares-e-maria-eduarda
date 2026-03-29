package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.List;
import java.util.UUID;

/**
 * Dados para atualizar a representacao completa de um GCE.
 */
public record UpdateGceInput(
        UUID id,
        UUID projectId,
        String name,
        String description,
        Boolean selected,
        List<GceNodeInput> nodes,
        List<GceEdgeInput> edges,
        List<GceRestrictionInput> restrictions
) {
}
