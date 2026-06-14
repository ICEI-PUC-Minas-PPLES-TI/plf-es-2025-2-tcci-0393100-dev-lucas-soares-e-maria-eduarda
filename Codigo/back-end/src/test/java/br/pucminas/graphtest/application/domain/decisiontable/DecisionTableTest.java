package br.pucminas.graphtest.application.domain.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionTableTest {

    @Test
    void shouldExposeDecisionTableElementEnumWithRule() {
        assertEquals(List.of(
                DecisionTableElementEnum.CONDITION,
                DecisionTableElementEnum.ACTION,
                DecisionTableElementEnum.RULE
        ), List.of(DecisionTableElementEnum.values()));
    }

    @Test
    void shouldStoreDecisionTableElementsInUnifiedList() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        DecisionTable table = createTable(
                List.of(cell(ruleId, conditionId, DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES),
                        cell(ruleId, actionId, DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.NO)),
                conditionId,
                actionId,
                ruleId
        );

        assertEquals(3, table.getElements().size());
        assertEquals(List.of(conditionId), table.getConditionElements().stream().map(DecisionTableElement::getId).toList());
        assertEquals(List.of(actionId), table.getActionElements().stream().map(DecisionTableElement::getId).toList());
        assertEquals(List.of(ruleId), table.getRuleElements().stream().map(DecisionTableElement::getId).toList());
    }

    @Test
    void shouldExposeConditionAndActionCellsFromUnifiedList() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        DecisionTableCell conditionCell = cell(ruleId, conditionId, DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES);
        DecisionTableCell actionCell = cell(ruleId, actionId, DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.NO);

        DecisionTable table = createTable(List.of(conditionCell, actionCell), conditionId, actionId, ruleId);

        assertSame(conditionCell, table.getConditionCells().getFirst());
        assertSame(actionCell, table.getActionCells().getFirst());
        assertSame(conditionCell, table.findConditionCell(ruleId, conditionId));
        assertSame(actionCell, table.findActionCell(ruleId, actionId));
    }

    @Test
    void shouldRejectConditionCellReferencingActionElement() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> createTable(
                List.of(cell(ruleId, actionId, DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES)),
                conditionId,
                actionId,
                ruleId
        ));
    }

    @Test
    void shouldRejectActionCellReferencingConditionElement() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> createTable(
                List.of(cell(ruleId, conditionId, DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.YES)),
                conditionId,
                actionId,
                ruleId
        ));
    }

    @Test
    void shouldRejectIrrelevantValueForActionCell() {
        assertThrows(IllegalArgumentException.class, () -> cell(
                UUID.randomUUID(),
                UUID.randomUUID(),
                DecisionTableElementEnum.ACTION,
                DecisionTableCellValueEnum.IRRELEVANT
        ));
    }

    @Test
    void shouldRejectCellWithRuleType() {
        assertThrows(IllegalArgumentException.class, () -> cell(
                UUID.randomUUID(),
                UUID.randomUUID(),
                DecisionTableElementEnum.RULE,
                DecisionTableCellValueEnum.YES
        ));
    }

    @Test
    void shouldRejectCellReferencingNonRuleAsRuleElement() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> createTable(
                List.of(cell(conditionId, actionId, DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.YES)),
                conditionId,
                actionId,
                UUID.randomUUID()
        ));
    }

    @Test
    void shouldExposeRulesInOutputFromRuleElements() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        DecisionTableOutput output = DecisionTableOutput.from(createTable(
                List.of(cell(ruleId, conditionId, DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES)),
                conditionId,
                actionId,
                ruleId
        ));

        assertEquals(ruleId, output.rules().getFirst().id());
        assertEquals("Regra", output.rules().getFirst().description());
        assertEquals(ruleId, output.conditionCells().getFirst().ruleId());
    }


    @Test
    void shouldValidateDecisionTableElementContractByType() {
        UUID tableId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> new DecisionTableElement(UUID.randomUUID(), tableId, "C1", null, null, 0, DecisionTableElementEnum.CONDITION));
        assertThrows(IllegalArgumentException.class,
                () -> new DecisionTableElement(UUID.randomUUID(), tableId, "A1", "Acao", "Descricao", 0, DecisionTableElementEnum.ACTION));
        assertThrows(IllegalArgumentException.class,
                () -> new DecisionTableElement(UUID.randomUUID(), tableId, "R1", null, null, 0, DecisionTableElementEnum.RULE));
        assertThrows(IllegalArgumentException.class,
                () -> new DecisionTableElement(UUID.randomUUID(), tableId, "R1", "Regra", "Descricao", 0, DecisionTableElementEnum.RULE));
    }

    @Test
    void shouldRejectDuplicatedRuleElementTypeIntersection() {
        UUID conditionId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> createTable(
                List.of(
                        cell(ruleId, conditionId, DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES),
                        cell(ruleId, conditionId, DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.NO)
                ),
                conditionId,
                actionId,
                ruleId
        ));
    }

    @Test
    void shouldRejectDuplicatedElementCodeByType() {
        UUID tableId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        DecisionTableElement condition1 = element(UUID.randomUUID(), tableId, "C1", 0, DecisionTableElementEnum.CONDITION);
        DecisionTableElement condition2 = element(UUID.randomUUID(), tableId, "C1", 1, DecisionTableElementEnum.CONDITION);

        assertThrows(IllegalArgumentException.class, () -> new DecisionTable(
                tableId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela",
                "Descricao",
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(condition1, condition2, element(UUID.randomUUID(), tableId, "A1", 0, DecisionTableElementEnum.ACTION), rule(ruleId, tableId)),
                List.of()
        ));
    }

    @Test
    void shouldRejectDuplicatedElementOrderByType() {
        UUID tableId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        DecisionTableElement action1 = element(UUID.randomUUID(), tableId, "A1", 0, DecisionTableElementEnum.ACTION);
        DecisionTableElement action2 = element(UUID.randomUUID(), tableId, "A2", 0, DecisionTableElementEnum.ACTION);

        assertThrows(IllegalArgumentException.class, () -> new DecisionTable(
                tableId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela",
                "Descricao",
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(element(UUID.randomUUID(), tableId, "C1", 0, DecisionTableElementEnum.CONDITION), action1, action2, rule(ruleId, tableId)),
                List.of()
        ));
    }

    private DecisionTable createTable(List<DecisionTableCell> cells,
                                      UUID conditionId,
                                      UUID actionId,
                                      UUID ruleId) {
        UUID tableId = UUID.randomUUID();
        return new DecisionTable(
                tableId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela",
                "Descricao",
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(
                        element(conditionId, tableId, "C1", 0, DecisionTableElementEnum.CONDITION),
                        element(actionId, tableId, "A1", 0, DecisionTableElementEnum.ACTION),
                        rule(ruleId, tableId)
                ),
                cells
        );
    }

    private DecisionTableElement element(UUID id,
                                         UUID tableId,
                                         String code,
                                         int orderIndex,
                                         DecisionTableElementEnum type) {
        return new DecisionTableElement(id, tableId, code, code + " label", orderIndex, type);
    }

    private DecisionTableElement rule(UUID id, UUID tableId) {
        return new DecisionTableElement(id, tableId, "R1", null, "Regra", 0, DecisionTableElementEnum.RULE);
    }

    private DecisionTableCell cell(UUID ruleId,
                                   UUID elementId,
                                   DecisionTableElementEnum type,
                                   DecisionTableCellValueEnum value) {
        return new DecisionTableCell(UUID.randomUUID(), ruleId, elementId, type, value);
    }
}
