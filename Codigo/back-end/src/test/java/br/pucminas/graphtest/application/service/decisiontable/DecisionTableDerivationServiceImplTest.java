package br.pucminas.graphtest.application.service.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.model.GceRestriction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisionTableDerivationServiceImplTest {

    private final DecisionTableDerivationServiceImpl service = new DecisionTableDerivationServiceImpl();

    @Test
    void shouldThrowWhenGraphIsNull() {
        assertThrows(NullPointerException.class, () -> service.derive(null, null));
    }

    @Test
    void shouldDeriveTwoRulesForSingleCauseDirectlyConnectedToEffect() {
        Gce graph = directCauseToEffectGraph(GceEdgeTypeEnum.IDENTITY);

        DecisionTable table = service.derive(graph, null);

        assertEquals(1, table.getConditionElements().size());
        assertEquals("C1", table.getConditionElements().getFirst().getCode());
        assertEquals(1, table.getActionElements().size());
        assertEquals("E1", table.getActionElements().getFirst().getCode());
        assertEquals(2, table.getRuleElements().size());
        assertEquals(DecisionTableSyncStatusEnum.UP_TO_DATE, table.getSyncStatus());
        assertNotNull(table.getSourceFingerprint());
        assertEquals(graph.getId(), table.getGceId());
        assertEquals(graph.getProjectId(), table.getProjectId());

        Map<String, DecisionTableCellValueEnum> actionByRuleCause = actionValueByConditionValue(table);
        assertEquals(DecisionTableCellValueEnum.NO, actionByRuleCause.get("NO"));
        assertEquals(DecisionTableCellValueEnum.YES, actionByRuleCause.get("YES"));
    }

    @Test
    void shouldInvertActionValueWhenEdgeIsNegated() {
        Gce graph = directCauseToEffectGraph(GceEdgeTypeEnum.NEGATED);

        DecisionTable table = service.derive(graph, null);

        Map<String, DecisionTableCellValueEnum> actionByRuleCause = actionValueByConditionValue(table);
        assertEquals(DecisionTableCellValueEnum.YES, actionByRuleCause.get("NO"));
        assertEquals(DecisionTableCellValueEnum.NO, actionByRuleCause.get("YES"));
    }

    @Test
    void shouldDeriveFourRulesWithAndOperatorAndOnlyTrueWhenBothCausesAreTrue() {
        Gce graph = twoCausesThroughOperatorGraph(GceOperatorTypeEnum.AND, List.of());

        DecisionTable table = service.derive(graph, null);

        assertEquals(4, table.getRuleElements().size());
        long trueActionRules = countActionYesRules(table);
        assertEquals(1, trueActionRules);
    }

    @Test
    void shouldDeriveFourRulesWithOrOperatorAndTrueWheneverAnyCauseIsTrue() {
        Gce graph = twoCausesThroughOperatorGraph(GceOperatorTypeEnum.OR, List.of());

        DecisionTable table = service.derive(graph, null);

        assertEquals(4, table.getRuleElements().size());
        long trueActionRules = countActionYesRules(table);
        assertEquals(3, trueActionRules);
    }

    @Test
    void shouldFilterOutRulesViolatingExclusiveRestriction() {
        UUID restrictionId = UUID.randomUUID();
        GceRestriction exclusive = new GceRestriction(restrictionId, RestrictionTypeEnum.EXCLUSIVE, List.of("C1", "C2"));
        Gce graph = twoCausesThroughOperatorGraph(GceOperatorTypeEnum.AND, List.of(exclusive));

        DecisionTable table = service.derive(graph, null);

        assertEquals(3, table.getRuleElements().size());
        assertTrue(table.getConditionCells().stream()
                .filter(cell -> cell.getValue() == DecisionTableCellValueEnum.YES)
                .collect(Collectors.groupingBy(cell -> cell.getRuleId(), Collectors.counting()))
                .values().stream()
                .allMatch(count -> count <= 1));
    }

    @Test
    void shouldFilterOutRulesViolatingRequireRestriction() {
        GceRestriction require = new GceRestriction(UUID.randomUUID(), RestrictionTypeEnum.REQUIRE, List.of("C1", "C2"));
        Gce graph = twoCausesThroughOperatorGraph(GceOperatorTypeEnum.AND, List.of(require));

        DecisionTable table = service.derive(graph, null);

        assertEquals(3, table.getRuleElements().size());
    }

    @Test
    void shouldReuseElementIdsAndCreatedAtWhenRederivingSameGraph() throws InterruptedException {
        Gce graph = directCauseToEffectGraph(GceEdgeTypeEnum.IDENTITY);

        DecisionTable firstDerivation = service.derive(graph, null);
        assertNotNull(firstDerivation.getCreatedAt());
        assertEquals(null, firstDerivation.getUpdatedAt());

        Thread.sleep(5);
        DecisionTable secondDerivation = service.derive(graph, firstDerivation);

        assertEquals(firstDerivation.getId(), secondDerivation.getId());
        assertEquals(firstDerivation.getCreatedAt(), secondDerivation.getCreatedAt());
        assertNotNull(secondDerivation.getUpdatedAt());
        assertEquals(firstDerivation.getConditionElements().getFirst().getId(),
                secondDerivation.getConditionElements().getFirst().getId());
        assertEquals(firstDerivation.getActionElements().getFirst().getId(),
                secondDerivation.getActionElements().getFirst().getId());
        assertEquals(
                firstDerivation.getRuleElements().stream().map(DecisionTableElement::getId).sorted().toList(),
                secondDerivation.getRuleElements().stream().map(DecisionTableElement::getId).sorted().toList()
        );
        assertEquals(firstDerivation.getSourceFingerprint(), secondDerivation.getSourceFingerprint());
    }

    @Test
    void shouldProduceDifferentFingerprintWhenGraphChanges() {
        Gce graph = directCauseToEffectGraph(GceEdgeTypeEnum.IDENTITY);
        DecisionTable original = service.derive(graph, null);

        Gce changedGraph = new Gce(
                graph.getId(),
                graph.getProjectId(),
                graph.getName(),
                graph.getDescription(),
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa alterada"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of()
        );

        DecisionTable derivedAgain = service.derive(changedGraph, null);

        assertNotEquals(original.getSourceFingerprint(), derivedAgain.getSourceFingerprint());
    }

    @Test
    void shouldProduceSameFingerprintRegardlessOfNodeInsertionOrder() {
        UUID projectId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        GceNode cause = GceNode.cause(UUID.randomUUID(), "C1", "Causa");
        GceNode effect = GceNode.effect(UUID.randomUUID(), "E1", "Efeito");
        GceEdge edge = new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY);

        Gce graphOrderOne = new Gce(graphId, projectId, "GCE", null, false, List.of(cause, effect), List.of(edge), List.of());
        Gce graphOrderTwo = new Gce(graphId, projectId, "GCE", null, false, List.of(effect, cause), List.of(edge), List.of());

        DecisionTable tableOne = service.derive(graphOrderOne, null);
        DecisionTable tableTwo = service.derive(graphOrderTwo, null);

        assertEquals(tableOne.getSourceFingerprint(), tableTwo.getSourceFingerprint());
    }

    @Test
    void shouldUseGraphUpdatedAtWhenPresentAsSourceGceUpdatedAt() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        Gce graph = new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GCE",
                null,
                false,
                List.of(GceNode.cause(UUID.randomUUID(), "C1", "Causa"), GceNode.effect(UUID.randomUUID(), "E1", "Efeito")),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of(),
                createdAt,
                updatedAt
        );

        DecisionTable table = service.derive(graph, null);

        assertEquals(updatedAt, table.getSourceGceUpdatedAt());
    }

    private Gce directCauseToEffectGraph(GceEdgeTypeEnum edgeType) {
        return new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", edgeType)),
                List.of()
        );
    }

    private Gce twoCausesThroughOperatorGraph(GceOperatorTypeEnum operatorType, List<GceRestriction> restrictions) {
        return new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.cause(UUID.randomUUID(), "C2", "Causa 2"),
                        GceNode.operator(UUID.randomUUID(), "O1", "Operador", operatorType),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(
                        new GceEdge(UUID.randomUUID(), "C1", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "C2", "O1", GceEdgeTypeEnum.IDENTITY),
                        new GceEdge(UUID.randomUUID(), "O1", "E1", GceEdgeTypeEnum.IDENTITY)
                ),
                restrictions
        );
    }

    private Map<String, DecisionTableCellValueEnum> actionValueByConditionValue(DecisionTable table) {
        UUID conditionId = table.getConditionElements().getFirst().getId();
        UUID actionId = table.getActionElements().getFirst().getId();

        return table.getRuleElements().stream().collect(Collectors.toMap(
                rule -> table.findConditionCell(rule.getId(), conditionId).getValue().name(),
                rule -> table.findActionCell(rule.getId(), actionId).getValue()
        ));
    }

    private long countActionYesRules(DecisionTable table) {
        UUID actionId = table.getActionElements().getFirst().getId();
        return table.getRuleElements().stream()
                .map(rule -> table.findActionCell(rule.getId(), actionId))
                .filter(cell -> cell.getValue() == DecisionTableCellValueEnum.YES)
                .count();
    }
}
