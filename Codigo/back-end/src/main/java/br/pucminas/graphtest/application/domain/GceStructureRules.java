package br.pucminas.graphtest.application.domain;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Reune as regras estruturais invariantes que definem um GCE valido.
 *
 * <p>Esta classe auxilia o agregado {@link Gce} mantendo a logica de
 * consistencia estrutural separada da logica de manipulacao de estado.</p>
 */
final class GceStructureRules {

    /**
     * Impede instanciacao.
     */
    private GceStructureRules() {
    }

    /**
     * Executa todas as verificacoes estruturais obrigatorias do modelo.
     *
     * @param graph grafo a ser validado
     */
    static void validate(Gce graph) {
        validateGraphReferences(graph);
        validateNodeCodes(graph);
        validateEdgeDirections(graph);
        validateNodeCardinality(graph);
        validateRestrictions(graph);
    }

    /**
     * Verifica se arestas e restricoes referenciam apenas nos existentes.
     *
     * @param graph grafo analisado
     */
    private static void validateGraphReferences(Gce graph) {
        for (GceEdge edge : graph.getEdges()) {
            if (graph.findNode(edge.getSourceNodeId()).isEmpty()) {
                throw new IllegalArgumentException("Aresta referencia sourceNodeId inexistente: " + edge.getSourceNodeId());
            }
            if (graph.findNode(edge.getTargetNodeId()).isEmpty()) {
                throw new IllegalArgumentException("Aresta referencia targetNodeId inexistente: " + edge.getTargetNodeId());
            }
        }

        for (GceRestriction restriction : graph.getRestrictions()) {
            for (UUID nodeId : restriction.getNodeIds()) {
                if (graph.findNode(nodeId).isEmpty()) {
                    throw new IllegalArgumentException("Restricao referencia no inexistente: " + nodeId);
                }
            }
        }
    }

    /**
     * Verifica se nao existem codigos duplicados de nos.
     *
     * @param graph grafo analisado
     */
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

    /**
     * Verifica se as direcoes das arestas respeitam a semantica do modelo.
     *
     * @param graph grafo analisado
     */
    private static void validateEdgeDirections(Gce graph) {
        for (GceEdge edge : graph.getEdges()) {
            GceNode sourceNode = graph.findNode(edge.getSourceNodeId()).orElseThrow();
            GceNode targetNode = graph.findNode(edge.getTargetNodeId()).orElseThrow();

            if (sourceNode.isEffect()) {
                throw new IllegalArgumentException("Efeito nao pode ser origem de aresta: " + sourceNode.getCode());
            }
            if (targetNode.isCause()) {
                throw new IllegalArgumentException("Causa nao pode ser destino de aresta: " + targetNode.getCode());
            }
        }
    }

    /**
     * Verifica cardinalidades estruturais que nao podem ser ambiguas no agregado.
     *
     * @param graph grafo analisado
     */
    private static void validateNodeCardinality(Gce graph) {
        for (GceNode node : graph.getNodes()) {
            if (!node.isEffect()) {
                continue;
            }

            int incomingEdges = graph.incomingEdges(node.getId()).size();
            if (incomingEdges > 1) {
                throw new IllegalArgumentException(
                        "Efeito deve possuir no maximo uma aresta de entrada direta. Utilize operador logico antes do efeito: "
                                + node.getCode()
                );
            }
        }
    }

    /**
     * Verifica se cada restricao referencia tipos de nos compativeis.
     *
     * @param graph grafo analisado
     */
    private static void validateRestrictions(Gce graph) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.isCauseRestriction()) {
                validateRestrictionNodesAreCauses(graph, restriction);
                continue;
            }
            validateRestrictionNodesAreEffects(graph, restriction);
        }
    }

    /**
     * Verifica se uma restricao de causa referencia apenas nos de causa.
     *
     * @param graph grafo analisado
     * @param restriction restricao em validacao
     */
    private static void validateRestrictionNodesAreCauses(Gce graph, GceRestriction restriction) {
        for (UUID nodeId : restriction.getNodeIds()) {
            GceNode node = graph.findNode(nodeId).orElseThrow();
            if (!node.isCause()) {
                throw new IllegalArgumentException("Restricao " + restriction.getType() + " deve referenciar apenas nos CAUSE.");
            }
        }
    }

    /**
     * Verifica se uma restricao de efeito referencia apenas nos de efeito.
     *
     * @param graph grafo analisado
     * @param restriction restricao em validacao
     */
    private static void validateRestrictionNodesAreEffects(Gce graph, GceRestriction restriction) {
        for (UUID nodeId : restriction.getNodeIds()) {
            GceNode node = graph.findNode(nodeId).orElseThrow();
            if (!node.isEffect()) {
                throw new IllegalArgumentException("Restricao " + restriction.getType() + " deve referenciar apenas nos EFFECT.");
            }
        }
    }
}
