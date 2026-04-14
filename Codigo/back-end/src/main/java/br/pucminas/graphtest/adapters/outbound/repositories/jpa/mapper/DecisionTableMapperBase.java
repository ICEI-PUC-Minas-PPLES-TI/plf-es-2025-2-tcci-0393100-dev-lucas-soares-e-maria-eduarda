package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableActionCellEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableActionEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableConditionCellEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableConditionEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableRuleEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableAction;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableActionCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCondition;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableConditionCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DecisionTableMapperBase implements BasePersistenceMapper<DecisionTable, JpaDecisionTableEntity> {

    @Override
    public JpaDecisionTableEntity toEntity(DecisionTable decisionTable) {
        if (decisionTable == null) {
            return null;
        }

        JpaDecisionTableEntity entity = new JpaDecisionTableEntity();
        entity.setId(decisionTable.getId());
        entity.setCreatedAt(decisionTable.getCreatedAt());
        entity.setUpdatedAt(decisionTable.getUpdatedAt());
        entity.setGceId(decisionTable.getGceId());
        entity.setProjectId(decisionTable.getProjectId());
        entity.setName(decisionTable.getName());
        entity.setDescription(decisionTable.getDescription());
        entity.setSourceFingerprint(decisionTable.getSourceFingerprint());
        entity.setSyncStatus(decisionTable.getSyncStatus());
        entity.setGeneratedAt(decisionTable.getGeneratedAt());
        entity.setSourceGceUpdatedAt(decisionTable.getSourceGceUpdatedAt());

        Map<UUID, JpaDecisionTableConditionEntity> conditionsById = new LinkedHashMap<>();
        for (DecisionTableCondition condition : decisionTable.getConditions()) {
            JpaDecisionTableConditionEntity conditionEntity = new JpaDecisionTableConditionEntity();
            conditionEntity.setId(condition.getId());
            conditionEntity.setCreatedAt(condition.getCreatedAt());
            conditionEntity.setUpdatedAt(condition.getUpdatedAt());
            conditionEntity.setDecisionTable(entity);
            conditionEntity.setCode(condition.getCode());
            conditionEntity.setLabel(condition.getLabel());
            conditionEntity.setOrderIndex(condition.getOrderIndex());
            conditionsById.put(condition.getId(), conditionEntity);
        }

        Map<UUID, JpaDecisionTableActionEntity> actionsById = new LinkedHashMap<>();
        for (DecisionTableAction action : decisionTable.getActions()) {
            JpaDecisionTableActionEntity actionEntity = new JpaDecisionTableActionEntity();
            actionEntity.setId(action.getId());
            actionEntity.setCreatedAt(action.getCreatedAt());
            actionEntity.setUpdatedAt(action.getUpdatedAt());
            actionEntity.setDecisionTable(entity);
            actionEntity.setCode(action.getCode());
            actionEntity.setLabel(action.getLabel());
            actionEntity.setOrderIndex(action.getOrderIndex());
            actionsById.put(action.getId(), actionEntity);
        }

        Map<UUID, JpaDecisionTableRuleEntity> rulesById = new LinkedHashMap<>();
        for (DecisionTableRule rule : decisionTable.getRules()) {
            JpaDecisionTableRuleEntity ruleEntity = new JpaDecisionTableRuleEntity();
            ruleEntity.setId(rule.getId());
            ruleEntity.setCreatedAt(rule.getCreatedAt());
            ruleEntity.setUpdatedAt(rule.getUpdatedAt());
            ruleEntity.setDecisionTable(entity);
            ruleEntity.setCode(rule.getCode());
            ruleEntity.setDescription(rule.getDescription());
            ruleEntity.setOrderIndex(rule.getOrderIndex());
            rulesById.put(rule.getId(), ruleEntity);
        }

        List<JpaDecisionTableConditionCellEntity> conditionCellEntities = new ArrayList<>();
        for (DecisionTableConditionCell conditionCell : decisionTable.getConditionCells()) {
            JpaDecisionTableConditionCellEntity cellEntity = new JpaDecisionTableConditionCellEntity();
            cellEntity.setId(conditionCell.getId());
            cellEntity.setCreatedAt(conditionCell.getCreatedAt());
            cellEntity.setUpdatedAt(conditionCell.getUpdatedAt());
            cellEntity.setDecisionTable(entity);
            cellEntity.setRule(rulesById.get(conditionCell.getRuleId()));
            cellEntity.setCondition(conditionsById.get(conditionCell.getConditionId()));
            cellEntity.setValue(conditionCell.getValue());
            conditionCellEntities.add(cellEntity);
        }

        List<JpaDecisionTableActionCellEntity> actionCellEntities = new ArrayList<>();
        for (DecisionTableActionCell actionCell : decisionTable.getActionCells()) {
            JpaDecisionTableActionCellEntity cellEntity = new JpaDecisionTableActionCellEntity();
            cellEntity.setId(actionCell.getId());
            cellEntity.setCreatedAt(actionCell.getCreatedAt());
            cellEntity.setUpdatedAt(actionCell.getUpdatedAt());
            cellEntity.setDecisionTable(entity);
            cellEntity.setRule(rulesById.get(actionCell.getRuleId()));
            cellEntity.setAction(actionsById.get(actionCell.getActionId()));
            cellEntity.setValue(actionCell.getValue());
            actionCellEntities.add(cellEntity);
        }

        entity.setConditions(new ArrayList<>(conditionsById.values()));
        entity.setActions(new ArrayList<>(actionsById.values()));
        entity.setRules(new ArrayList<>(rulesById.values()));
        entity.setConditionCells(conditionCellEntities);
        entity.setActionCells(actionCellEntities);
        return entity;
    }

    @Override
    public DecisionTable toDomain(JpaDecisionTableEntity entity) {
        if (entity == null) {
            return null;
        }

        List<DecisionTableCondition> conditions = entity.getConditions() == null
                ? List.of()
                : entity.getConditions().stream()
                .map(condition -> new DecisionTableCondition(
                        condition.getId(),
                        entity.getId(),
                        condition.getCode(),
                        condition.getLabel(),
                        condition.getOrderIndex(),
                        condition.getCreatedAt(),
                        condition.getUpdatedAt()
                ))
                .toList();

        List<DecisionTableAction> actions = entity.getActions() == null
                ? List.of()
                : entity.getActions().stream()
                .map(action -> new DecisionTableAction(
                        action.getId(),
                        entity.getId(),
                        action.getCode(),
                        action.getLabel(),
                        action.getOrderIndex(),
                        action.getCreatedAt(),
                        action.getUpdatedAt()
                ))
                .toList();

        List<DecisionTableRule> rules = entity.getRules() == null
                ? List.of()
                : entity.getRules().stream()
                .map(rule -> new DecisionTableRule(
                        rule.getId(),
                        entity.getId(),
                        rule.getCode(),
                        rule.getDescription(),
                        rule.getOrderIndex(),
                        rule.getCreatedAt(),
                        rule.getUpdatedAt()
                ))
                .toList();

        List<DecisionTableConditionCell> conditionCells = entity.getConditionCells() == null
                ? List.of()
                : entity.getConditionCells().stream()
                .map(cell -> new DecisionTableConditionCell(
                        cell.getId(),
                        cell.getRule().getId(),
                        cell.getCondition().getId(),
                        cell.getValue(),
                        cell.getCreatedAt(),
                        cell.getUpdatedAt()
                ))
                .toList();

        List<DecisionTableActionCell> actionCells = entity.getActionCells() == null
                ? List.of()
                : entity.getActionCells().stream()
                .map(cell -> new DecisionTableActionCell(
                        cell.getId(),
                        cell.getRule().getId(),
                        cell.getAction().getId(),
                        cell.getValue(),
                        cell.getCreatedAt(),
                        cell.getUpdatedAt()
                ))
                .toList();

        return new DecisionTable(
                entity.getId(),
                entity.getGceId(),
                entity.getProjectId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSourceFingerprint(),
                entity.getSyncStatus(),
                entity.getGeneratedAt(),
                entity.getSourceGceUpdatedAt(),
                conditions,
                actions,
                rules,
                conditionCells,
                actionCells,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
