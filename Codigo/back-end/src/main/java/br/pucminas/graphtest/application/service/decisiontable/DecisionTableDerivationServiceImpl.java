package br.pucminas.graphtest.application.service.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableActionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableAction;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableActionCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCondition;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableConditionCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableRule;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableDerivationService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Implementacao concreta do servico responsavel por derivar tabelas de decisao a partir de GCEs.
 */
public class DecisionTableDerivationServiceImpl implements DecisionTableDerivationService {

    @Override
    public DecisionTable derive(Gce graph, DecisionTable currentTable) {
        Objects.requireNonNull(graph, "graph e obrigatorio.");

        LocalDateTime now = LocalDateTime.now();
        UUID tableId = resolveTableId(currentTable);
        List<GceNode> orderedCauses = orderedCauseNodes(graph);
        List<GceNode> orderedEffects = orderedEffectNodes(graph);

        List<DecisionTableCondition> conditions = buildConditions(currentTable, tableId, orderedCauses, now);
        List<DecisionTableAction> actions = buildActions(currentTable, tableId, orderedEffects, now);
        DerivedRulesData derivedRulesData = buildRulesAndCells(graph, currentTable, tableId, conditions, actions, now);

        return buildDecisionTable(
                graph,
                currentTable,
                tableId,
                conditions,
                actions,
                derivedRulesData.rules(),
                derivedRulesData.conditionCells(),
                derivedRulesData.actionCells(),
                now
        );
    }

    private UUID resolveTableId(DecisionTable currentTable) {
        return currentTable != null && currentTable.getId() != null
                ? currentTable.getId()
                : UUID.randomUUID();
    }

    private List<GceNode> orderedCauseNodes(Gce graph) {
        return graph.getCauseNodes().stream()
                .sorted(Comparator.comparing(GceNode::getCode))
                .toList();
    }

    private List<GceNode> orderedEffectNodes(Gce graph) {
        return graph.getEffectNodes().stream()
                .sorted(Comparator.comparing(GceNode::getCode))
                .toList();
    }

    private List<DecisionTableCondition> buildConditions(DecisionTable currentTable,
                                                         UUID tableId,
                                                         List<GceNode> orderedCauses,
                                                         LocalDateTime now) {
        List<DecisionTableCondition> conditions = new ArrayList<>();

        for (int index = 0; index < orderedCauses.size(); index++) {
            GceNode cause = orderedCauses.get(index);
            UUID existingId = findExistingConditionId(currentTable, cause.getCode());

            conditions.add(new DecisionTableCondition(
                    ensureId(existingId),
                    tableId,
                    cause.getCode(),
                    cause.getLabel(),
                    index,
                    findExistingConditionCreatedAt(currentTable, cause.getCode(), now),
                    existingId != null ? now : null
            ));
        }

        return conditions;
    }

    private List<DecisionTableAction> buildActions(DecisionTable currentTable,
                                                   UUID tableId,
                                                   List<GceNode> orderedEffects,
                                                   LocalDateTime now) {
        List<DecisionTableAction> actions = new ArrayList<>();

        for (int index = 0; index < orderedEffects.size(); index++) {
            GceNode effect = orderedEffects.get(index);
            UUID existingId = findExistingActionId(currentTable, effect.getCode());

            actions.add(new DecisionTableAction(
                    ensureId(existingId),
                    tableId,
                    effect.getCode(),
                    effect.getLabel(),
                    index,
                    findExistingActionCreatedAt(currentTable, effect.getCode(), now),
                    existingId != null ? now : null
            ));
        }

        return actions;
    }

    private DerivedRulesData buildRulesAndCells(Gce graph,
                                                DecisionTable currentTable,
                                                UUID tableId,
                                                List<DecisionTableCondition> conditions,
                                                List<DecisionTableAction> actions,
                                                LocalDateTime now) {
        List<Map<String, Boolean>> validAssignments = enumerateValidAssignments(graph, orderedCauseNodes(graph));
        List<DecisionTableRule> rules = new ArrayList<>();
        List<DecisionTableConditionCell> conditionCells = new ArrayList<>();
        List<DecisionTableActionCell> actionCells = new ArrayList<>();

        for (int index = 0; index < validAssignments.size(); index++) {
            RuleAssembly ruleAssembly = assembleRule(
                    graph,
                    currentTable,
                    tableId,
                    conditions,
                    actions,
                    validAssignments.get(index),
                    index,
                    now
            );
            rules.add(ruleAssembly.rule());
            conditionCells.addAll(ruleAssembly.conditionCells());
            actionCells.addAll(ruleAssembly.actionCells());
        }

        return new DerivedRulesData(rules, conditionCells, actionCells);
    }

    private RuleAssembly assembleRule(Gce graph,
                                      DecisionTable currentTable,
                                      UUID tableId,
                                      List<DecisionTableCondition> conditions,
                                      List<DecisionTableAction> actions,
                                      Map<String, Boolean> assignment,
                                      int ruleIndex,
                                      LocalDateTime now) {
        String ruleCode = ruleCode(ruleIndex);
        DecisionTableRule rule = buildRule(currentTable, tableId, ruleCode, ruleIndex, now);
        Map<String, Boolean> resolvedValues = resolveAssignment(graph, assignment);

        return new RuleAssembly(
                rule,
                buildConditionCells(currentTable, rule, conditions, assignment, now),
                buildActionCells(currentTable, rule, actions, resolvedValues, now)
        );
    }

    private String ruleCode(int ruleIndex) {
        return "R" + (ruleIndex + 1);
    }

    private DecisionTableRule buildRule(DecisionTable currentTable,
                                        UUID tableId,
                                        String ruleCode,
                                        int orderIndex,
                                        LocalDateTime now) {
        UUID existingId = findExistingRuleId(currentTable, ruleCode);
        return new DecisionTableRule(
                ensureId(existingId),
                tableId,
                ruleCode,
                "",
                orderIndex,
                findExistingRuleCreatedAt(currentTable, ruleCode, now),
                existingId != null ? now : null
        );
    }

    private Map<String, Boolean> resolveAssignment(Gce graph, Map<String, Boolean> assignment) {
        Map<String, Boolean> resolvedValues = evaluateGraph(graph, assignment);
        if (resolvedValues == null) {
            throw new IllegalArgumentException("Nao foi possivel derivar a tabela de decisao para o GCE informado.");
        }
        return resolvedValues;
    }

    private List<DecisionTableConditionCell> buildConditionCells(DecisionTable currentTable,
                                                                 DecisionTableRule rule,
                                                                 List<DecisionTableCondition> conditions,
                                                                 Map<String, Boolean> assignment,
                                                                 LocalDateTime now) {
        List<DecisionTableConditionCell> cells = new ArrayList<>();

        for (DecisionTableCondition condition : conditions) {
            UUID existingId = findExistingConditionCellId(currentTable, rule.getCode(), condition.getCode());
            cells.add(new DecisionTableConditionCell(
                    ensureId(existingId),
                    rule.getId(),
                    condition.getId(),
                    assignment.getOrDefault(condition.getCode(), false)
                            ? DecisionTableConditionValueEnum.YES
                            : DecisionTableConditionValueEnum.NO,
                    findExistingConditionCellCreatedAt(currentTable, rule.getCode(), condition.getCode(), now),
                    existingId != null ? now : null
            ));
        }

        return cells;
    }

    private List<DecisionTableActionCell> buildActionCells(DecisionTable currentTable,
                                                           DecisionTableRule rule,
                                                           List<DecisionTableAction> actions,
                                                           Map<String, Boolean> resolvedValues,
                                                           LocalDateTime now) {
        List<DecisionTableActionCell> cells = new ArrayList<>();

        for (DecisionTableAction action : actions) {
            UUID existingId = findExistingActionCellId(currentTable, rule.getCode(), action.getCode());
            cells.add(new DecisionTableActionCell(
                    ensureId(existingId),
                    rule.getId(),
                    action.getId(),
                    resolvedValues.getOrDefault(action.getCode(), false)
                            ? DecisionTableActionValueEnum.YES
                            : DecisionTableActionValueEnum.NO,
                    findExistingActionCellCreatedAt(currentTable, rule.getCode(), action.getCode(), now),
                    existingId != null ? now : null
            ));
        }

        return cells;
    }

    private DecisionTable buildDecisionTable(Gce graph,
                                             DecisionTable currentTable,
                                             UUID tableId,
                                             List<DecisionTableCondition> conditions,
                                             List<DecisionTableAction> actions,
                                             List<DecisionTableRule> rules,
                                             List<DecisionTableConditionCell> conditionCells,
                                             List<DecisionTableActionCell> actionCells,
                                             LocalDateTime now) {
        return new DecisionTable(
                tableId,
                graph.getId(),
                graph.getProjectId(),
                graph.getName(),
                graph.getDescription(),
                buildFingerprint(graph),
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                effectiveGraphUpdatedAt(graph),
                conditions,
                actions,
                rules,
                conditionCells,
                actionCells,
                currentTable != null ? currentTable.getCreatedAt() : now,
                currentTable != null ? now : null
        );
    }

    private LocalDateTime effectiveGraphUpdatedAt(Gce graph) {
        return graph.getUpdatedAt() != null ? graph.getUpdatedAt() : graph.getCreatedAt();
    }

    private UUID ensureId(UUID id) {
        return id != null ? id : UUID.randomUUID();
    }

    private List<Map<String, Boolean>> enumerateValidAssignments(Gce graph, List<GceNode> causes) {
        List<Map<String, Boolean>> validAssignments = new ArrayList<>();
        int combinations = totalCombinations(causes.size());

        for (int mask = 0; mask < combinations; mask++) {
            Map<String, Boolean> assignment = buildAssignment(causes, mask);
            if (respectsCauseRestrictions(graph, assignment)) {
                validAssignments.add(assignment);
            }
        }

        return validAssignments;
    }

    private int totalCombinations(int causeCount) {
        return 1 << causeCount;
    }

    private Map<String, Boolean> buildAssignment(List<GceNode> causes, int mask) {
        Map<String, Boolean> assignment = new LinkedHashMap<>();
        for (int i = 0; i < causes.size(); i++) {
            assignment.put(causes.get(i).getCode(), isBitEnabled(mask, i));
        }
        return assignment;
    }

    private boolean isBitEnabled(int mask, int bitIndex) {
        return (mask & (1 << bitIndex)) != 0;
    }

    private boolean respectsCauseRestrictions(Gce graph, Map<String, Boolean> assignment) {
        for (GceRestriction restriction : graph.getRestrictions()) {
            if (shouldIgnoreInCauseValidation(restriction)) {
                continue;
            }

            if (!restrictionSatisfied(restriction, assignment)) {
                return false;
            }
        }

        return true;
    }

    private boolean shouldIgnoreInCauseValidation(GceRestriction restriction) {
        return restriction.getType() == RestrictionTypeEnum.MASKS;
    }

    private boolean restrictionSatisfied(GceRestriction restriction, Map<String, Boolean> assignment) {
        List<Boolean> values = restrictionValues(restriction, assignment);

        return switch (restriction.getType()) {
            case EXCLUSIVE -> atMostOneTrue(values);
            case INCLUSIVE -> atLeastOneTrue(values);
            case ONE_AND_ONLY_ONE -> exactlyOneTrue(values);
            case REQUIRE -> respectsRequire(restriction, assignment);
            case MASKS -> true;
        };
    }

    private List<Boolean> restrictionValues(GceRestriction restriction, Map<String, Boolean> assignment) {
        return restriction.getNodeCodes().stream()
                .map(assignment::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean atMostOneTrue(List<Boolean> values) {
        return trueCount(values) <= 1;
    }

    private boolean atLeastOneTrue(List<Boolean> values) {
        return values.stream().anyMatch(Boolean::booleanValue);
    }

    private boolean exactlyOneTrue(List<Boolean> values) {
        return trueCount(values) == 1;
    }

    private long trueCount(List<Boolean> values) {
        return values.stream().filter(Boolean::booleanValue).count();
    }

    private boolean respectsRequire(GceRestriction restriction, Map<String, Boolean> assignment) {
        boolean first = assignment.getOrDefault(restriction.firstNode(), false);
        boolean second = assignment.getOrDefault(restriction.secondNode(), false);
        return !first || second;
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

            List<Boolean> inputs = resolveNodeInputs(graph, node, values);
            if (inputs == null) {
                return null;
            }

            values.put(node.getCode(), resolveNodeValue(node, inputs));
        }

        return values;
    }

    private List<Boolean> resolveNodeInputs(Gce graph, GceNode node, Map<String, Boolean> values) {
        List<Boolean> inputs = new ArrayList<>();

        for (GceEdge edge : graph.incomingEdges(node.getCode())) {
            Boolean sourceValue = values.get(edge.getSourceNodeCode());
            if (sourceValue == null) {
                return null;
            }
            inputs.add(edge.getType().apply(sourceValue));
        }

        return inputs;
    }

    private boolean resolveNodeValue(GceNode node, List<Boolean> inputs) {
        if (node.isOperator()) {
            return resolveOperator(node.getOperatorType(), inputs);
        }

        if (inputs.size() != 1) {
            throw new IllegalArgumentException("No nao operador precisa receber exatamente uma entrada.");
        }

        return inputs.get(0);
    }

    private boolean resolveOperator(GceOperatorTypeEnum operatorType, List<Boolean> inputs) {
        return switch (operatorType) {
            case AND -> inputs.stream().allMatch(Boolean::booleanValue);
            case OR -> inputs.stream().anyMatch(Boolean::booleanValue);
        };
    }

    private List<GceNode> topologicalOrder(Gce graph) {
        Map<String, Integer> inDegree = buildInDegree(graph);
        Map<String, List<String>> adjacency = buildAdjacency(graph);
        Deque<String> queue = buildRootQueue(inDegree);
        List<GceNode> ordered = traverseTopologically(graph, inDegree, adjacency, queue);
        return hasVisitedAllNodes(graph, ordered) ? ordered : List.of();
    }

    private Map<String, Integer> buildInDegree(Gce graph) {
        Map<String, Integer> inDegree = new HashMap<>();
        for (GceNode node : graph.getNodes()) {
            inDegree.put(node.getCode(), 0);
        }
        for (GceEdge edge : graph.getEdges()) {
            inDegree.compute(edge.getTargetNodeCode(), (key, value) -> value == null ? 1 : value + 1);
        }
        return inDegree;
    }

    private Map<String, List<String>> buildAdjacency(Gce graph) {
        Map<String, List<String>> adjacency = new HashMap<>();
        for (GceNode node : graph.getNodes()) {
            adjacency.put(node.getCode(), new ArrayList<>());
        }
        for (GceEdge edge : graph.getEdges()) {
            adjacency.computeIfAbsent(edge.getSourceNodeCode(), ignored -> new ArrayList<>())
                    .add(edge.getTargetNodeCode());
        }
        return adjacency;
    }

    private Deque<String> buildRootQueue(Map<String, Integer> inDegree) {
        Deque<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }
        return queue;
    }

    private List<GceNode> traverseTopologically(Gce graph,
                                                Map<String, Integer> inDegree,
                                                Map<String, List<String>> adjacency,
                                                Deque<String> queue) {
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

        return ordered;
    }

    private boolean hasVisitedAllNodes(Gce graph, List<GceNode> ordered) {
        return ordered.size() == graph.getNodes().size();
    }

    private String buildFingerprint(Gce graph) {
        StringBuilder builder = new StringBuilder();
        appendGraphMetadata(graph, builder);
        appendNodes(graph, builder);
        appendEdges(graph, builder);
        appendRestrictions(graph, builder);
        return hash(builder.toString());
    }

    private void appendGraphMetadata(Gce graph, StringBuilder builder) {
        builder.append(graph.getProjectId()).append('|')
                .append(graph.getName()).append('|')
                .append(graph.getDescription()).append('|');
    }

    private void appendNodes(Gce graph, StringBuilder builder) {
        graph.getNodes().stream()
                .sorted(Comparator.comparing(GceNode::getCode))
                .forEach(node -> appendNode(builder, node));
    }

    private void appendNode(StringBuilder builder, GceNode node) {
        builder.append("N:")
                .append(node.getCode()).append(':')
                .append(node.getLabel()).append(':')
                .append(node.getType()).append(':')
                .append(node.getOperatorType())
                .append('|');
    }

    private void appendEdges(Gce graph, StringBuilder builder) {
        graph.getEdges().stream()
                .sorted(Comparator.comparing(GceEdge::getSourceNodeCode)
                        .thenComparing(GceEdge::getTargetNodeCode)
                        .thenComparing(edge -> edge.getType().name()))
                .forEach(edge -> appendEdge(builder, edge));
    }

    private void appendEdge(StringBuilder builder, GceEdge edge) {
        builder.append("E:")
                .append(edge.getSourceNodeCode()).append(':')
                .append(edge.getTargetNodeCode()).append(':')
                .append(edge.getType())
                .append('|');
    }

    private void appendRestrictions(Gce graph, StringBuilder builder) {
        graph.getRestrictions().stream()
                .sorted(Comparator.comparing((GceRestriction restriction) -> restriction.getType().name())
                        .thenComparing(restriction -> String.join(",", restriction.getNodeCodes())))
                .forEach(restriction -> appendRestriction(builder, restriction));
    }

    private void appendRestriction(StringBuilder builder, GceRestriction restriction) {
        builder.append("R:")
                .append(restriction.getType()).append(':')
                .append(String.join(",", restriction.getNodeCodes()))
                .append('|');
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Nao foi possivel calcular o fingerprint da tabela de decisao.", exception);
        }
    }

    private UUID findExistingConditionId(DecisionTable currentTable, String code) {
        if (currentTable == null) {
            return null;
        }
        return currentTable.getConditions().stream()
                .filter(condition -> condition.getCode().equals(code))
                .map(DecisionTableCondition::getId)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime findExistingConditionCreatedAt(DecisionTable currentTable, String code, LocalDateTime fallback) {
        if (currentTable == null) {
            return fallback;
        }
        return currentTable.getConditions().stream()
                .filter(condition -> condition.getCode().equals(code))
                .map(DecisionTableCondition::getCreatedAt)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(fallback);
    }

    private UUID findExistingActionId(DecisionTable currentTable, String code) {
        if (currentTable == null) {
            return null;
        }
        return currentTable.getActions().stream()
                .filter(action -> action.getCode().equals(code))
                .map(DecisionTableAction::getId)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime findExistingActionCreatedAt(DecisionTable currentTable, String code, LocalDateTime fallback) {
        if (currentTable == null) {
            return fallback;
        }
        return currentTable.getActions().stream()
                .filter(action -> action.getCode().equals(code))
                .map(DecisionTableAction::getCreatedAt)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(fallback);
    }

    private UUID findExistingRuleId(DecisionTable currentTable, String code) {
        if (currentTable == null) {
            return null;
        }
        return currentTable.getRules().stream()
                .filter(rule -> rule.getCode().equals(code))
                .map(DecisionTableRule::getId)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime findExistingRuleCreatedAt(DecisionTable currentTable, String code, LocalDateTime fallback) {
        if (currentTable == null) {
            return fallback;
        }
        return currentTable.getRules().stream()
                .filter(rule -> rule.getCode().equals(code))
                .map(DecisionTableRule::getCreatedAt)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(fallback);
    }

    private UUID findExistingConditionCellId(DecisionTable currentTable, String ruleCode, String conditionCode) {
        if (currentTable == null) {
            return null;
        }
        UUID ruleId = findExistingRuleId(currentTable, ruleCode);
        UUID conditionId = findExistingConditionId(currentTable, conditionCode);
        if (ruleId == null || conditionId == null) {
            return null;
        }
        DecisionTableConditionCell cell = currentTable.findConditionCell(ruleId, conditionId);
        return cell != null ? cell.getId() : null;
    }

    private LocalDateTime findExistingConditionCellCreatedAt(DecisionTable currentTable,
                                                             String ruleCode,
                                                             String conditionCode,
                                                             LocalDateTime fallback) {
        if (currentTable == null) {
            return fallback;
        }
        UUID ruleId = findExistingRuleId(currentTable, ruleCode);
        UUID conditionId = findExistingConditionId(currentTable, conditionCode);
        if (ruleId == null || conditionId == null) {
            return fallback;
        }
        DecisionTableConditionCell cell = currentTable.findConditionCell(ruleId, conditionId);
        return cell != null && cell.getCreatedAt() != null ? cell.getCreatedAt() : fallback;
    }

    private UUID findExistingActionCellId(DecisionTable currentTable, String ruleCode, String actionCode) {
        if (currentTable == null) {
            return null;
        }
        UUID ruleId = findExistingRuleId(currentTable, ruleCode);
        UUID actionId = findExistingActionId(currentTable, actionCode);
        if (ruleId == null || actionId == null) {
            return null;
        }
        DecisionTableActionCell cell = currentTable.findActionCell(ruleId, actionId);
        return cell != null ? cell.getId() : null;
    }

    private LocalDateTime findExistingActionCellCreatedAt(DecisionTable currentTable,
                                                          String ruleCode,
                                                          String actionCode,
                                                          LocalDateTime fallback) {
        if (currentTable == null) {
            return fallback;
        }
        UUID ruleId = findExistingRuleId(currentTable, ruleCode);
        UUID actionId = findExistingActionId(currentTable, actionCode);
        if (ruleId == null || actionId == null) {
            return fallback;
        }
        DecisionTableActionCell cell = currentTable.findActionCell(ruleId, actionId);
        return cell != null && cell.getCreatedAt() != null ? cell.getCreatedAt() : fallback;
    }

    private record DerivedRulesData(
            List<DecisionTableRule> rules,
            List<DecisionTableConditionCell> conditionCells,
            List<DecisionTableActionCell> actionCells
    ) {
    }

    private record RuleAssembly(
            DecisionTableRule rule,
            List<DecisionTableConditionCell> conditionCells,
            List<DecisionTableActionCell> actionCells
    ) {
    }
}
