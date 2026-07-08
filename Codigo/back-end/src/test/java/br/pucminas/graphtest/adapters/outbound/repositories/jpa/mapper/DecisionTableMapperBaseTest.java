package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableCellEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableElementEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.project.JpaProjectEntity;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DecisionTableMapperBaseTest {

    private final DecisionTableMapperBase mapper = new DecisionTableMapperBase();

    @Test
    void shouldReturnNullWhenConvertingNullDecisionTableToEntity() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldReturnNullWhenConvertingNullEntityToDomain() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void shouldConvertDecisionTableWithElementsAndCellsToEntity() {
        UUID tableId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        DecisionTableElement condition = new DecisionTableElement(UUID.randomUUID(), tableId, "C1", "Condicao", 0, DecisionTableElementEnum.CONDITION);
        DecisionTableElement action = new DecisionTableElement(UUID.randomUUID(), tableId, "E1", "Acao", 0, DecisionTableElementEnum.ACTION);
        DecisionTableElement rule = new DecisionTableElement(UUID.randomUUID(), tableId, "R1", null, "", 0, DecisionTableElementEnum.RULE);
        DecisionTableCell conditionCell = new DecisionTableCell(UUID.randomUUID(), rule.getId(), condition.getId(), DecisionTableElementEnum.CONDITION, DecisionTableCellValueEnum.YES);
        DecisionTableCell actionCell = new DecisionTableCell(UUID.randomUUID(), rule.getId(), action.getId(), DecisionTableElementEnum.ACTION, DecisionTableCellValueEnum.NO);
        DecisionTable table = new DecisionTable(
                tableId,
                UUID.randomUUID(),
                projectId,
                "Tabela",
                "Descricao",
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(condition, action, rule),
                List.of(conditionCell, actionCell)
        );

        JpaDecisionTableEntity entity = mapper.toEntity(table);

        assertEquals(tableId, entity.getId());
        assertEquals(projectId, entity.getProject().getId());
        assertEquals("Tabela", entity.getName());
        assertEquals(3, entity.getElements().size());
        assertEquals(2, entity.getCells().size());
        assertEquals(entity, entity.getElements().getFirst().getDecisionTable());
        assertEquals(entity, entity.getCells().getFirst().getDecisionTable());
    }

    @Test
    void shouldConvertEntityWithElementsAndCellsToDomain() {
        UUID tableId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        JpaProjectEntity project = new JpaProjectEntity();
        project.setId(projectId);

        JpaDecisionTableElementEntity conditionEntity = new JpaDecisionTableElementEntity();
        conditionEntity.setId(UUID.randomUUID());
        conditionEntity.setCode("C1");
        conditionEntity.setLabel("Condicao");
        conditionEntity.setOrderIndex(0);
        conditionEntity.setType(DecisionTableElementEnum.CONDITION);

        JpaDecisionTableElementEntity ruleEntity = new JpaDecisionTableElementEntity();
        ruleEntity.setId(UUID.randomUUID());
        ruleEntity.setCode("R1");
        ruleEntity.setDescription("");
        ruleEntity.setOrderIndex(0);
        ruleEntity.setType(DecisionTableElementEnum.RULE);

        JpaDecisionTableCellEntity cellEntity = new JpaDecisionTableCellEntity();
        cellEntity.setId(UUID.randomUUID());
        cellEntity.setRuleElement(ruleEntity);
        cellEntity.setDecisionTableElement(conditionEntity);
        cellEntity.setType(DecisionTableElementEnum.CONDITION);
        cellEntity.setValue(DecisionTableCellValueEnum.YES);

        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        entity.setId(tableId);
        entity.setProject(project);
        entity.setName("Tabela");
        entity.setDescription("Descricao");
        entity.setSourceFingerprint("fingerprint");
        entity.setSyncStatus(DecisionTableSyncStatusEnum.UP_TO_DATE);
        entity.setElements(List.of(conditionEntity, ruleEntity));
        entity.setCells(List.of(cellEntity));

        DecisionTable table = mapper.toDomain(entity);

        assertEquals(tableId, table.getId());
        assertEquals(projectId, table.getProjectId());
        assertEquals(1, table.getConditionElements().size());
        assertEquals(1, table.getRuleElements().size());
        assertEquals(1, table.getConditionCells().size());
    }

    @Test
    void shouldConvertEntityWithoutElementsOrCellsToDomain() {
        UUID tableId = UUID.randomUUID();
        JpaProjectEntity project = new JpaProjectEntity();
        project.setId(UUID.randomUUID());
        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        entity.setId(tableId);
        entity.setProject(project);
        entity.setName("Tabela");
        entity.setSourceFingerprint("fingerprint");
        entity.setSyncStatus(DecisionTableSyncStatusEnum.UP_TO_DATE);
        entity.setElements(null);
        entity.setCells(null);

        DecisionTable table = mapper.toDomain(entity);

        assertEquals(0, table.getElements().size());
        assertEquals(0, table.getCells().size());
    }
}
