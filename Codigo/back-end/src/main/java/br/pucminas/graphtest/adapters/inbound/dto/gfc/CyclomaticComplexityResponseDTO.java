package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.List;
import java.util.UUID;

public record CyclomaticComplexityResponseDTO(
        UUID gfcId,
        int nodesCount,
        int edgesCount,
        int predicateNodesCount,
        int cyclomaticComplexityByEdgesAndNodes,
        int cyclomaticComplexityByPredicateNodes,
        String formulaByEdgesAndNodes,
        String formulaByPredicateNodes,
        List<String> warnings
) {
}
