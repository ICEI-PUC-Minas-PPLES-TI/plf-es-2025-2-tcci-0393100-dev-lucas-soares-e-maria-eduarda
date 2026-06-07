package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableCellEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableElementEntity;
import br.pucminas.graphtest.adapters.outbound.entities.jpa.decisiontable.JpaDecisionTableEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCell;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;
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
        entity.setSourceGceUpdatedAt(decisionTable.getSourceGceUpdatedAt());

        Map<UUID, JpaDecisionTableElementEntity> elementsById = mapElements(decisionTable, entity);
        entity.setElements(new ArrayList<>(elementsById.values()));
        entity.setCells(mapCells(decisionTable, entity, elementsById));
        return entity;
    }

    private Map<UUID, JpaDecisionTableElementEntity> mapElements(
            DecisionTable decisionTable,
            JpaDecisionTableEntity entity
    ) {
        Map<UUID, JpaDecisionTableElementEntity> elementsById = new LinkedHashMap<>();
        for (DecisionTableElement element : decisionTable.getElements()) {
            JpaDecisionTableElementEntity elementEntity = new JpaDecisionTableElementEntity();
            elementEntity.setId(element.getId());
            elementEntity.setCreatedAt(element.getCreatedAt());
            elementEntity.setUpdatedAt(element.getUpdatedAt());
            elementEntity.setDecisionTable(entity);
            elementEntity.setCode(element.getCode());
            elementEntity.setLabel(element.getLabel());
            elementEntity.setDescription(element.getDescription());
            elementEntity.setOrderIndex(element.getOrderIndex());
            elementEntity.setType(element.getType());
            elementsById.put(element.getId(), elementEntity);
        }
        return elementsById;
    }

    private List<JpaDecisionTableCellEntity> mapCells(
            DecisionTable decisionTable,
            JpaDecisionTableEntity entity,
            Map<UUID, JpaDecisionTableElementEntity> elementsById
    ) {
        List<JpaDecisionTableCellEntity> cellEntities = new ArrayList<>();
        for (DecisionTableCell cell : decisionTable.getCells()) {
            JpaDecisionTableCellEntity cellEntity = new JpaDecisionTableCellEntity();
            cellEntity.setId(cell.getId());
            cellEntity.setCreatedAt(cell.getCreatedAt());
            cellEntity.setUpdatedAt(cell.getUpdatedAt());
            cellEntity.setDecisionTable(entity);
            cellEntity.setRuleElement(elementsById.get(cell.getRuleElementId()));
            cellEntity.setDecisionTableElement(elementsById.get(cell.getDecisionTableElementId()));
            cellEntity.setType(cell.getType());
            cellEntity.setValue(cell.getValue());
            cellEntities.add(cellEntity);
        }
        return cellEntities;
    }

    @Override
    public DecisionTable toDomain(JpaDecisionTableEntity entity) {
        if (entity == null) {
            return null;
        }

        List<DecisionTableElement> elements = entity.getElements() == null
                ? List.of()
                : entity.getElements().stream()
                .map(element -> new DecisionTableElement(
                        element.getId(),
                        entity.getId(),
                        element.getCode(),
                        element.getLabel(),
                        element.getDescription(),
                        element.getOrderIndex(),
                        element.getType(),
                        element.getCreatedAt(),
                        element.getUpdatedAt()
                ))
                .toList();

        List<DecisionTableCell> cells = entity.getCells() == null
                ? List.of()
                : entity.getCells().stream()
                .map(cell -> new DecisionTableCell(
                        cell.getId(),
                        cell.getRuleElement().getId(),
                        cell.getDecisionTableElement().getId(),
                        cell.getType(),
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
                entity.getSourceGceUpdatedAt(),
                elements,
                cells,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
