package br.pucminas.graphtest.application.port.input.gfc.records;

import java.util.List;
import java.util.UUID;

public record CyclomaticComplexityOutput(
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
