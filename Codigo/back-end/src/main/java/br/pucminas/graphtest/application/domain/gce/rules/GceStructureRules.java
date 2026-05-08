package br.pucminas.graphtest.application.domain.gce.rules;

import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;

import java.util.List;
import java.util.Map;

/**
 * Reune as regras estruturais invariantes que definem um GCE valido.
 */
public final class GceStructureRules {

    private GceStructureRules() {
    }

    public static void validate(Gce graph) {
        validateGraphReferences(graph);
        validateNodeCodes(graph);
        validateEdgeDirections(graph);
        validateNodeCardinality(graph);
        validateRestrictions(graph);
    }

    private static void validateGraphReferences(Gce graph) {
        for (GceEdge edge : graph.getEdges()) {
            if (graph.findNode(edge.getSourceNodeCode()).isEmpty()) {
                throw new IllegalArgumentException("A aresta referencia um nó de origem inexistente: " + edge.getSourceNodeCode());
            }
            if (graph.findNode(edge.getTargetNodeCode()).isEmpty()) {
                throw new IllegalArgumentException("A aresta referencia um nó de destino inexistente: " + edge.getTargetNodeCode());
            }
        }

        for (GceRestriction restriction : graph.getRestrictions()) {
            for (String nodeCode : restriction.getNodeCodes()) {
                if (graph.findNode(nodeCode).isEmpty()) {
                    throw new IllegalArgumentException("A restrição referencia um nó inexistente: " + nodeCode);
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
            throw new IllegalArgumentException("Há códigos de nós duplicados: " + String.join(", ", duplicateCodes));
        }
    }

    private static void validateEdgeDirections(Gce graph) {
        for (GceEdge edge : graph.getEdges()) {
            GceNode sourceNode = graph.findNode(edge.getSourceNodeCode()).orElseThrow();
            GceNode targetNode = graph.findNode(edge.getTargetNodeCode()).orElseThrow();

            if (sourceNode.isEffect()) {
                throw new IllegalArgumentException("Um efeito não pode ser origem de aresta: " + sourceNode.getCode());
            }
            if (sourceNode.isCause() && !targetNode.isOperator() && !targetNode.isEffect()) {
                throw new IllegalArgumentException("Uma causa só pode se conectar a um operador ou a um efeito: " + sourceNode.getCode());
            }
            if (targetNode.isCause()) {
                throw new IllegalArgumentException("Uma causa não pode ser destino de aresta: " + targetNode.getCode());
            }
            if (targetNode.isEffect() && !sourceNode.isOperator() && !sourceNode.isCause()) {
                throw new IllegalArgumentException("Um efeito só pode receber aresta de um operador ou de uma causa: " + targetNode.getCode());
            }
        }
    }

    private static void validateNodeCardinality(Gce graph) {
        for (GceNode node : graph.getNodes()) {
            if (node.isOperator()) {
                int incomingEdges = graph.incomingEdges(node.getCode()).size();
                if (incomingEdges > 2) {
                    throw new IllegalArgumentException("Um operador deve possuir no máximo duas arestas de entrada: " + node.getCode());
                }
            }

            if (!node.isEffect()) {
                continue;
            }

            int incomingEdges = graph.incomingEdges(node.getCode()).size();
            if (incomingEdges > 1) {
                throw new IllegalArgumentException(
                        "Um efeito deve possuir no máximo uma aresta de entrada direta. Utilize um operador lógico antes do efeito: "
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
                throw new IllegalArgumentException("A restrição " + restriction.getType() + " deve referenciar apenas causas.");
            }
        }
    }

    private static void validateRestrictionNodesAreEffects(Gce graph, GceRestriction restriction) {
        for (String nodeCode : restriction.getNodeCodes()) {
            GceNode node = graph.findNode(nodeCode).orElseThrow();
            if (!node.isEffect()) {
                throw new IllegalArgumentException("A restrição " + restriction.getType() + " deve referenciar apenas efeitos.");
            }
        }
    }
}
