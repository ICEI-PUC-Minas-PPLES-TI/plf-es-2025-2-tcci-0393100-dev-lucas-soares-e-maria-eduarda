package br.pucminas.graphtest.application.port.input.gce.records;

import java.util.List;
import java.util.UUID;

public record ValidateGceInput(
        UUID projectId,
        String name,
        String description,
        Boolean selected,
        List<GceNodeInput> nodes,
        List<GceEdgeInput> edges,
        List<GceRestrictionInput> restrictions
) {
}
