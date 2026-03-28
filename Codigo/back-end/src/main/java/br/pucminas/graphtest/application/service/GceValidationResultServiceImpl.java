package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.Gce;
import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.GceNode;
import br.pucminas.graphtest.application.domain.GceRestriction;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceMessage;
import br.pucminas.graphtest.application.port.input.gce.records.ValidationGceOutput;
import br.pucminas.graphtest.application.service.interfaces.GceValidationResultService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementa a validacao completa de um Grafo de Causa e Efeito.
 *
 * <p>O processo combina verificacoes estruturais, topologicas e semanticas para
 * identificar inconsistencias no modelo antes de seu uso pelos casos de uso.</p>
 */
public class GceValidationResultServiceImpl implements GceValidationResultService {

    private static final int MAX_CAUSES_FOR_FULL_ENUMERATION = 16;

    /**
     * Executa a validacao estrutural e semantica completa de um GCE.
     *
     * @param graph agregado de GCE a ser analisado
     * @return resultado consolidado da validacao
     */
    @Override
    public ValidationGceOutput validate(Gce graph) {
        List<ValidationGceMessage> errors = new ArrayList<>();
        List<ValidationGceMessage> warnings = new ArrayList<>();

        validateBasicStructure(graph, errors);
        validateNodeTypesAndCardinality(graph, errors, warnings);
        validateRestrictions(graph, errors);

        if (!hasCriticalErrors(errors)) {
            validateAcyclic(graph, errors);
        }

        if (!hasCriticalErrors(errors)) {
            validateReachability(graph, errors);
        }

        if (!hasCriticalErrors(errors)) {
            validateSemanticConsistency(graph, errors, warnings);
        }

        return new ValidationGceOutput(errors, warnings);
    }

    /**
     * Indica se ja foram encontrados erros que impedem validacoes subsequentes.
     *
     * @param errors erros acumulados da validacao
     * @return {@code true} quando existem erros criticos
     */
    private boolean hasCriticalErrors(List<ValidationGceMessage> errors) {
        return !errors.isEmpty();
    }

    /**
     * Executa verificacoes basicas de existencia e referencia no modelo.
     *
     * @param graph grafo analisado
     * @param errors erros acumulados
     */
    private void validateBasicStructure(Gce graph, List<ValidationGceMessage> errors) {
        if (graph.getNodes().isEmpty()) {
            addError(errors, "GCE_001", "O grafo deve possuir ao menos um no.");
        }

        if (graph.getCauseNodes().isEmpty()) {
            addError(errors, "GCE_002", "O grafo deve possuir ao menos uma causa.");
        }

        if (graph.getEffectNodes().isEmpty()) {
            addError(errors, "GCE_003", "O grafo deve possuir ao menos um efeito.");
        }

        graph.countNodeCodes().forEach((code, count) -> {
            if (count > 1) {
                addError(errors, "GCE_004", "Codigo de no duplicado: " + code);
            }
        });

        for (GceEdge edge : graph.getEdges()) {
            if (graph.findNode(edge.getSourceNodeId()).isEmpty()) {
                addError(errors, "GCE_005", "Aresta " + edge.getId() + " possui origem inexistente.");
            }
            if (graph.findNode(edge.getTargetNodeId()).isEmpty()) {
                addError(errors, "GCE_006", "Aresta " + edge.getId() + " possui destino inexistente.");
            }
        }
    }

    /**
     * Valida cardinalidade e posicionamento de causas, efeitos e operadores.
     *
     * @param graph grafo analisado
     * @param errors erros acumulados
     * @param warnings avisos acumulados
     */
    private void validateNodeTypesAndCardinality(Gce graph, List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        for (GceNode node : graph.getNodes()) {
            List<GceEdge> incoming = graph.incomingEdges(node.getId());
            List<GceEdge> outgoing = graph.outgoingEdges(node.getId());

            if (node.isCause()) {
                if (!incoming.isEmpty()) {
                    addError(errors, "GCE_007", "Causa " + node.getCode() + " nao pode possuir arestas de entrada.");
                }
                if (outgoing.isEmpty()) {
                    addWarning(warnings, "GCE_008", "Causa " + node.getCode() + " nao contribui para nenhum destino.");
                }
            }

            if (node.isEffect()) {
                if (!outgoing.isEmpty()) {
                    addError(errors, "GCE_009", "Efeito " + node.getCode() + " nao pode possuir arestas de saida.");
                }
                if (incoming.isEmpty()) {
                    addError(errors, "GCE_010", "Efeito " + node.getCode() + " deve possuir ao menos uma aresta de entrada.");
                }
            }

            if (node.isOperator()) {
                if (incoming.size() < 2) {
                    addError(errors, "GCE_011", "Operador " + node.getCode() + " deve possuir pelo menos duas entradas.");
                }
                if (outgoing.size() != 1) {
                    addError(errors, "GCE_012", "Operador " + node.getCode() + " deve possuir exatamente uma saida.");
                }
            }
        }
    }

    /**
     * Verifica se as restricoes referenciam apenas nos compativeis.
     *
     * @param graph grafo analisado
     * @param errors erros acumulados
     */
    private void validateRestrictions(Gce graph, List<ValidationGceMessage> errors) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            List<GceNode> nodes = restriction.getNodeIds().stream()
                    .map(graph::findNode)
                    .flatMap(Optional::stream)
                    .toList();

            if (nodes.size() != restriction.getNodeIds().size()) {
                addError(errors, "GCE_013", "Restricao " + restriction.getId() + " referencia no inexistente.");
                continue;
            }

            switch (restriction.getType()) {
                case EXCLUSIVE, INCLUSIVE, ONE_AND_ONLY_ONE, REQUIRE -> {
                    boolean allCauses = nodes.stream().allMatch(GceNode::isCause);
                    if (!allCauses) {
                        addError(errors, "GCE_014", "Restricoes E, I, O e R devem referenciar apenas causas.");
                    }
                }
                case MASKS -> {
                    boolean allEffects = nodes.stream().allMatch(GceNode::isEffect);
                    if (!allEffects) {
                        addError(errors, "GCE_015", "Restricao M deve referenciar apenas efeitos.");
                    }
                }
            }
        }
    }

    /**
     * Verifica se o grafo e aciclico.
     *
     * @param graph grafo analisado
     * @param errors erros acumulados
     */
    private void validateAcyclic(Gce graph, List<ValidationGceMessage> errors) {
        Map<UUID, List<UUID>> adjacency = new HashMap<>();
        for (GceNode node : graph.getNodes()) {
            adjacency.put(node.getId(), new ArrayList<>());
        }

        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeId(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeId());
        }

        Set<UUID> visited = new HashSet<>();
        Set<UUID> stack = new HashSet<>();

        for (GceNode node : graph.getNodes()) {
            if (detectCycle(node.getId(), adjacency, visited, stack)) {
                addError(errors, "GCE_016", "O grafo possui ciclo, o que torna a avaliacao logica ambigua.");
                return;
            }
        }
    }

    /**
     * Percorre recursivamente o grafo para detectar ciclos.
     *
     * @param current no atual da busca
     * @param adjacency lista de adjacencia do grafo
     * @param visited nos ja visitados
     * @param stack pilha logica da busca em profundidade
     * @return {@code true} quando um ciclo e encontrado
     */
    private boolean detectCycle(UUID current,
                                Map<UUID, List<UUID>> adjacency,
                                Set<UUID> visited,
                                Set<UUID> stack) {
        if (stack.contains(current)) {
            return true;
        }
        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        stack.add(current);

        for (UUID next : adjacency.getOrDefault(current, List.of())) {
            if (detectCycle(next, adjacency, visited, stack)) {
                return true;
            }
        }

        stack.remove(current);
        return false;
    }

    /**
     * Verifica se todos os efeitos sao alcancaveis a partir de alguma causa.
     *
     * @param graph grafo analisado
     * @param errors erros acumulados
     */
    private void validateReachability(Gce graph, List<ValidationGceMessage> errors) {
        Set<UUID> reachable = new HashSet<>();
        Deque<UUID> queue = new ArrayDeque<>();

        for (GceNode cause : graph.getCauseNodes()) {
            queue.add(cause.getId());
            reachable.add(cause.getId());
        }

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            for (GceEdge edge : graph.outgoingEdges(current)) {
                if (reachable.add(edge.getTargetNodeId())) {
                    queue.add(edge.getTargetNodeId());
                }
            }
        }

        for (GceNode effect : graph.getEffectNodes()) {
            if (!reachable.contains(effect.getId())) {
                addError(errors, "GCE_017", "Efeito " + effect.getCode() + " nao e alcancavel a partir de nenhuma causa.");
            }
        }
    }

    /**
     * Executa a validacao semantica por enumeracao de combinacoes de causas.
     *
     * @param graph grafo analisado
     * @param errors erros acumulados
     * @param warnings avisos acumulados
     */
    private void validateSemanticConsistency(Gce graph, List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        List<GceNode> causes = graph.getCauseNodes();
        if (causes.size() > MAX_CAUSES_FOR_FULL_ENUMERATION) {
            addWarning(warnings, "GCE_018", "O modelo possui muitas causas para enumeracao completa. A validacao semantica exaustiva foi pulada.");
            return;
        }

        List<Map<UUID, Boolean>> assignments = enumerateAssignments(causes);
        boolean hasAnyValidAssignment = false;

        for (Map<UUID, Boolean> assignment : assignments) {
            if (!respectsCauseRestrictions(graph, assignment)) {
                continue;
            }

            Map<UUID, Boolean> allValues = evaluateGraph(graph, assignment);
            if (allValues == null) {
                continue;
            }

            if (!respectsMaskRestrictions(graph, allValues)) {
                continue;
            }

            hasAnyValidAssignment = true;
            break;
        }

        if (!hasAnyValidAssignment) {
            addError(
                    errors,
                    "GCE_019",
                    "O modelo e semanticamente inconsistente: nao existe combinacao valida de causas que satisfaca simultaneamente a estrutura e as restricoes."
            );
        }
    }

    /**
     * Registra um erro de validacao na colecao acumulada.
     *
     * @param errors lista de erros acumulados
     * @param code codigo identificador da regra violada
     * @param message descricao textual do problema encontrado
     */
    private void addError(List<ValidationGceMessage> errors, String code, String message) {
        errors.add(new ValidationGceMessage(code, message));
    }

    /**
     * Registra um aviso de validacao na colecao acumulada.
     *
     * @param warnings lista de avisos acumulados
     * @param code codigo identificador do aviso
     * @param message descricao textual do ponto observado
     */
    private void addWarning(List<ValidationGceMessage> warnings, String code, String message) {
        warnings.add(new ValidationGceMessage(code, message));
    }

    /**
     * Gera todas as atribuicoes booleanas possiveis para o conjunto de causas.
     *
     * @param causes lista de causas do grafo
     * @return lista de atribuicoes possiveis
     */
    private List<Map<UUID, Boolean>> enumerateAssignments(List<GceNode> causes) {
        int size = causes.size();
        int combinations = 1 << size;
        List<Map<UUID, Boolean>> assignments = new ArrayList<>(combinations);

        for (int mask = 0; mask < combinations; mask++) {
            Map<UUID, Boolean> assignment = new HashMap<>();
            for (int i = 0; i < size; i++) {
                boolean value = (mask & (1 << i)) != 0;
                assignment.put(causes.get(i).getId(), value);
            }
            assignments.add(assignment);
        }

        return assignments;
    }

    /**
     * Verifica se uma atribuicao de causas respeita as restricoes entre causas.
     *
     * @param graph grafo analisado
     * @param assignment atribuicao candidata
     * @return {@code true} quando a atribuicao e permitida
     */
    private boolean respectsCauseRestrictions(Gce graph, Map<UUID, Boolean> assignment) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.getType() == RestrictionTypeEnum.MASKS) {
                continue;
            }

            List<Boolean> values = restriction.getNodeIds().stream()
                    .map(assignment::get)
                    .filter(Objects::nonNull)
                    .toList();

            switch (restriction.getType()) {
                case EXCLUSIVE -> {
                    long trueCount = values.stream().filter(Boolean::booleanValue).count();
                    if (trueCount > 1) {
                        return false;
                    }
                }
                case INCLUSIVE -> {
                    boolean anyTrue = values.stream().anyMatch(Boolean::booleanValue);
                    if (!anyTrue) {
                        return false;
                    }
                }
                case ONE_AND_ONLY_ONE -> {
                    long trueCount = values.stream().filter(Boolean::booleanValue).count();
                    if (trueCount != 1) {
                        return false;
                    }
                }
                case REQUIRE -> {
                    boolean first = assignment.getOrDefault(restriction.firstNode(), false);
                    boolean second = assignment.getOrDefault(restriction.secondNode(), false);
                    if (first && !second) {
                        return false;
                    }
                }
                case MASKS -> {
                }
            }
        }

        return true;
    }

    /**
     * Verifica se os efeitos avaliados respeitam as restricoes de mascaramento.
     *
     * @param graph grafo analisado
     * @param allValues valores calculados para todos os nos
     * @return {@code true} quando nao ha violacao de mascaramento
     */
    private boolean respectsMaskRestrictions(Gce graph, Map<UUID, Boolean> allValues) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.getType() != RestrictionTypeEnum.MASKS) {
                continue;
            }

            boolean masker = allValues.getOrDefault(restriction.firstNode(), false);
            boolean masked = allValues.getOrDefault(restriction.secondNode(), false);

            if (masker && masked) {
                return false;
            }
        }

        return true;
    }

    /**
     * Avalia o grafo inteiro a partir dos valores fixados para as causas.
     *
     * @param graph grafo analisado
     * @param causeValues valores atribuídos as causas
     * @return mapa com valores avaliados para todos os nos ou {@code null} quando a avaliacao falha
     */
    private Map<UUID, Boolean> evaluateGraph(Gce graph, Map<UUID, Boolean> causeValues) {
        Map<UUID, Boolean> values = new HashMap<>(causeValues);

        List<GceNode> ordered = topologicalOrder(graph);
        if (ordered.isEmpty()) {
            return null;
        }

        for (GceNode node : ordered) {
            if (node.isCause()) {
                continue;
            }

            List<GceEdge> incoming = graph.incomingEdges(node.getId());
            List<Boolean> inputs = new ArrayList<>();

            for (GceEdge edge : incoming) {
                Boolean sourceValue = values.get(edge.getSourceNodeId());
                if (sourceValue == null) {
                    return null;
                }
                inputs.add(edge.getType().apply(sourceValue));
            }

            boolean resolved;
            if (node.isOperator()) {
                resolved = resolveOperator(node.getOperatorType(), inputs);
            } else {
                if (inputs.size() != 1) {
                    return null;
                }
                resolved = inputs.get(0);
            }

            values.put(node.getId(), resolved);
        }

        return values;
    }

    /**
     * Resolve o valor de um operador logico com base em suas entradas.
     *
     * @param operatorType tipo do operador
     * @param inputs entradas booleanas do operador
     * @return resultado da operacao
     */
    private boolean resolveOperator(GceOperatorTypeEnum operatorType, List<Boolean> inputs) {
        return switch (operatorType) {
            case AND -> inputs.stream().allMatch(Boolean::booleanValue);
            case OR -> inputs.stream().anyMatch(Boolean::booleanValue);
        };
    }

    /**
     * Calcula uma ordenacao topologica dos nos do grafo.
     *
     * @param graph grafo analisado
     * @return lista ordenada dos nos ou lista vazia quando ha ciclo
     */
    private List<GceNode> topologicalOrder(Gce graph) {
        Map<UUID, Integer> inDegree = new HashMap<>();
        Map<UUID, List<UUID>> adjacency = new HashMap<>();

        for (GceNode node : graph.getNodes()) {
            inDegree.put(node.getId(), 0);
            adjacency.put(node.getId(), new ArrayList<>());
        }

        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeId(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeId());
            inDegree.compute(edge.getTargetNodeId(), (key, value) -> value == null ? 1 : value + 1);
        }

        Deque<UUID> queue = new ArrayDeque<>();
        for (Map.Entry<UUID, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<GceNode> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            graph.findNode(current).ifPresent(ordered::add);

            for (UUID next : adjacency.getOrDefault(current, List.of())) {
                int newValue = inDegree.compute(next, (key, value) -> value - 1);
                if (newValue == 0) {
                    queue.add(next);
                }
            }
        }

        if (ordered.size() != graph.getNodes().size()) {
            return List.of();
        }

        return ordered;
    }
}
