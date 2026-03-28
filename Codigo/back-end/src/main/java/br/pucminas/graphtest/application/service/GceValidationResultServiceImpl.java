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

/**
 * Implementa a validacao completa de um Grafo de Causa e Efeito.
 */
public class GceValidationResultServiceImpl implements GceValidationResultService {

    private static final int MAX_CAUSES_FOR_FULL_ENUMERATION = 16;

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

    private boolean hasCriticalErrors(List<ValidationGceMessage> errors) {
        return !errors.isEmpty();
    }

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
            if (graph.findNode(edge.getSourceNodeCode()).isEmpty()) {
                addError(errors, "GCE_005", "Aresta possui origem inexistente: " + edge.getSourceNodeCode());
            }
            if (graph.findNode(edge.getTargetNodeCode()).isEmpty()) {
                addError(errors, "GCE_006", "Aresta possui destino inexistente: " + edge.getTargetNodeCode());
            }
        }
    }

    private void validateNodeTypesAndCardinality(Gce graph, List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        for (GceNode node : graph.getNodes()) {
            List<GceEdge> incoming = graph.incomingEdges(node.getCode());
            List<GceEdge> outgoing = graph.outgoingEdges(node.getCode());

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

    private void validateRestrictions(Gce graph, List<ValidationGceMessage> errors) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            List<GceNode> nodes = restriction.getNodeCodes().stream()
                    .map(graph::findNode)
                    .flatMap(Optional::stream)
                    .toList();

            if (nodes.size() != restriction.getNodeCodes().size()) {
                addError(errors, "GCE_013", "Restricao referencia no inexistente.");
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

    private void validateAcyclic(Gce graph, List<ValidationGceMessage> errors) {
        Map<String, List<String>> adjacency = new HashMap<>();
        for (GceNode node : graph.getNodes()) {
            adjacency.put(node.getCode(), new ArrayList<>());
        }

        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeCode(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeCode());
        }

        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();

        for (GceNode node : graph.getNodes()) {
            if (detectCycle(node.getCode(), adjacency, visited, stack)) {
                addError(errors, "GCE_016", "O grafo possui ciclo, o que torna a avaliacao logica ambigua.");
                return;
            }
        }
    }

    private boolean detectCycle(String current,
                                Map<String, List<String>> adjacency,
                                Set<String> visited,
                                Set<String> stack) {
        if (stack.contains(current)) {
            return true;
        }
        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        stack.add(current);

        for (String next : adjacency.getOrDefault(current, List.of())) {
            if (detectCycle(next, adjacency, visited, stack)) {
                return true;
            }
        }

        stack.remove(current);
        return false;
    }

    private void validateReachability(Gce graph, List<ValidationGceMessage> errors) {
        Set<String> reachable = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();

        for (GceNode cause : graph.getCauseNodes()) {
            queue.add(cause.getCode());
            reachable.add(cause.getCode());
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (GceEdge edge : graph.outgoingEdges(current)) {
                if (reachable.add(edge.getTargetNodeCode())) {
                    queue.add(edge.getTargetNodeCode());
                }
            }
        }

        for (GceNode effect : graph.getEffectNodes()) {
            if (!reachable.contains(effect.getCode())) {
                addError(errors, "GCE_017", "Efeito " + effect.getCode() + " nao e alcancavel a partir de nenhuma causa.");
            }
        }
    }

    private void validateSemanticConsistency(Gce graph, List<ValidationGceMessage> errors, List<ValidationGceMessage> warnings) {
        List<GceNode> causes = graph.getCauseNodes();
        if (causes.size() > MAX_CAUSES_FOR_FULL_ENUMERATION) {
            addWarning(warnings, "GCE_018", "O modelo possui muitas causas para enumeracao completa. A validacao semantica exaustiva foi pulada.");
            return;
        }

        List<Map<String, Boolean>> assignments = enumerateAssignments(causes);
        boolean hasAnyValidAssignment = false;

        for (Map<String, Boolean> assignment : assignments) {
            if (!respectsCauseRestrictions(graph, assignment)) {
                continue;
            }

            Map<String, Boolean> allValues = evaluateGraph(graph, assignment);
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

    private void addError(List<ValidationGceMessage> errors, String code, String message) {
        errors.add(new ValidationGceMessage(code, message));
    }

    private void addWarning(List<ValidationGceMessage> warnings, String code, String message) {
        warnings.add(new ValidationGceMessage(code, message));
    }

    private List<Map<String, Boolean>> enumerateAssignments(List<GceNode> causes) {
        int size = causes.size();
        int combinations = 1 << size;
        List<Map<String, Boolean>> assignments = new ArrayList<>(combinations);

        for (int mask = 0; mask < combinations; mask++) {
            Map<String, Boolean> assignment = new HashMap<>();
            for (int i = 0; i < size; i++) {
                boolean value = (mask & (1 << i)) != 0;
                assignment.put(causes.get(i).getCode(), value);
            }
            assignments.add(assignment);
        }

        return assignments;
    }

    private boolean respectsCauseRestrictions(Gce graph, Map<String, Boolean> assignment) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (restriction.getType() == RestrictionTypeEnum.MASKS) {
                continue;
            }

            List<Boolean> values = restriction.getNodeCodes().stream()
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

    private boolean respectsMaskRestrictions(Gce graph, Map<String, Boolean> allValues) {
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

    private Map<String, Boolean> evaluateGraph(Gce graph, Map<String, Boolean> causeValues) {
        Map<String, Boolean> values = new HashMap<>(causeValues);

        List<GceNode> ordered = topologicalOrder(graph);
        if (ordered.isEmpty()) {
            return null;
        }

        for (GceNode node : ordered) {
            if (node.isCause()) {
                continue;
            }

            List<GceEdge> incoming = graph.incomingEdges(node.getCode());
            List<Boolean> inputs = new ArrayList<>();

            for (GceEdge edge : incoming) {
                Boolean sourceValue = values.get(edge.getSourceNodeCode());
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

            values.put(node.getCode(), resolved);
        }

        return values;
    }

    private boolean resolveOperator(GceOperatorTypeEnum operatorType, List<Boolean> inputs) {
        return switch (operatorType) {
            case AND -> inputs.stream().allMatch(Boolean::booleanValue);
            case OR -> inputs.stream().anyMatch(Boolean::booleanValue);
        };
    }

    private List<GceNode> topologicalOrder(Gce graph) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> adjacency = new HashMap<>();

        for (GceNode node : graph.getNodes()) {
            inDegree.put(node.getCode(), 0);
            adjacency.put(node.getCode(), new ArrayList<>());
        }

        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeCode(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeCode());
            inDegree.compute(edge.getTargetNodeCode(), (key, value) -> value == null ? 1 : value + 1);
        }

        Deque<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<GceNode> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            graph.findNode(current).ifPresent(ordered::add);

            for (String next : adjacency.getOrDefault(current, List.of())) {
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
