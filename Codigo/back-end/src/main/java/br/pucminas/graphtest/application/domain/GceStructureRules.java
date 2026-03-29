package br.pucminas.graphtest.application.domain;

import java.util.List;
import java.util.Map;

/**
 * Reune as regras estruturais invariantes que definem um GCE valido.
 */
final class GceStructureRules {

    private GceStructureRules() {
    }

    static void validate(Gce graph) {
        validateGraphReferences(graph);
        validateNodeCodes(graph);
        validateEdgeDirections(graph);
        validateNodeCardinality(graph);
        validateRestrictions(graph);
    }

    private static void validateGraphReferences(Gce graph) {
        for (GceEdge edge : graph.getEdges()) {
            if (graph.findNode(edge.getSourceNodeCode()).isEmpty()) {
                throw new IllegalArgumentException("Aresta referencia sourceNodeCode inexistente: " + edge.getSourceNodeCode());
            }
            if (graph.findNode(edge.getTargetNodeCode()).isEmpty()) {
                throw new IllegalArgumentException("Aresta referencia targetNodeCode inexistente: " + edge.getTargetNodeCode());
            }
        }

        for (GceRestriction restriction : graph.getRestrictions()) {
            for (String nodeCode : restriction.getNodeCodes()) {
                if (graph.findNode(nodeCode).isEmpty()) {
                    throw new IllegalArgumentException("Restricao referencia no inexistente: " + nodeCode);
                }
            }
        }
    }

    private static void validateNodeCodes(Gce graph) {
        List<String> duplicateCodes = graph.countNodeCodes().entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        if (!duplicateCodes.isEmpty()) {
            throw new IllegalArgumentException("Codigos de nos duplicados: " + String.join(", ", duplicateCodes));
        }
    }

    private static void validateEdgeDirections(Gce graph) {
        for (GceEdge edge : graph.getEdges()) {
            GceNode sourceNode = graph.findNode(edge.getSourceNodeCode()).orElseThrow();
            GceNode targetNode = graph.findNode(edge.getTargetNodeCode()).orElseThrow();

            if (sourceNode.isEffect()) {
                throw new IllegalArgumentException("Efeito nao pode ser origem de aresta: " + sourceNode.getCode());
            }
            if (sourceNode.isCause() && !targetNode.isOperator()) {
                throw new IllegalArgumentException("Causa so pode se conectar a operador: " + sourceNode.getCode());
            }
            if (targetNode.isCause()) {
                throw new IllegalArgumentException("Causa nao pode ser destino de aresta: " + targetNode.getCode());
            }
            if (targetNode.isEffect() && !sourceNode.isOperator()) {
                throw new IllegalArgumentException("Efeito so pode receber aresta de operador: " + targetNode.getCode());
            }
        }
    }

    private static void validateNodeCardinality(Gce graph) {
        for (GceNode node : graph.getNodes()) {
            if (node.isOperator()) {
                int incomingEdges = graph.incomingEdges(node.getCode()).size();
                if (incomingEdges > 2) {
                    throw new IllegalArgumentException("Operador deve possuir no maximo duas arestas de entrada: " + node.getCode());
                }
            }

            if (!node.isEffect()) {
                continue;
            }

            int incomingEdges = graph.incomingEdges(node.getCode()).size();
            if (incomingEdges > 1) {
                throw new IllegalArgumentException(
                        "Efeito deve possuir no maximo uma aresta de entrada direta. Utilize operador logico antes do efeito: "
                                + node.getCode()
                );
            }
        }
    }

    private static void validateRestrictions(Gce graph) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.isCauseRestriction()) {
                validateRestrictionNodesAreCauses(graph, restriction);
                continue;
            }
            validateRestrictionNodesAreEffects(graph, restriction);
        }
    }

    private static void validateRestrictionNodesAreCauses(Gce graph, GceRestriction restriction) {
        for (String nodeCode : restriction.getNodeCodes()) {
            GceNode node = graph.findNode(nodeCode).orElseThrow();
            if (!node.isCause()) {
                throw new IllegalArgumentException("Restricao " + restriction.getType() + " deve referenciar apenas nos CAUSE.");
            }
        }
    }

    private static void validateRestrictionNodesAreEffects(Gce graph, GceRestriction restriction) {
        for (String nodeCode : restriction.getNodeCodes()) {
            GceNode node = graph.findNode(nodeCode).orElseThrow();
            if (!node.isEffect()) {
                throw new IllegalArgumentException("Restricao " + restriction.getType() + " deve referenciar apenas nos EFFECT.");
            }
        }
    }
}
