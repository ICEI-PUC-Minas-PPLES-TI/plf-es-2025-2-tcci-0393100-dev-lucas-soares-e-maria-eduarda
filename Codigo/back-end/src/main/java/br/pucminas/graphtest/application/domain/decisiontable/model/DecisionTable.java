package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Agregado raiz que representa uma tabela de decisao derivada de um GCE.
 */
public class DecisionTable extends BaseEntity {

    private UUID gceId;
    private UUID projectId;
    private String name;
    private String description;
    private String sourceFingerprint;
    private DecisionTableSyncStatusEnum syncStatus;
    private LocalDateTime sourceGceUpdatedAt;
    private final List<DecisionTableElement> elements;
    private final List<DecisionTableCell> cells;

    public DecisionTable(UUID id,
                         UUID gceId,
                         UUID projectId,
                         String name,
                         String description,
                         String sourceFingerprint,
                         DecisionTableSyncStatusEnum syncStatus,
                         LocalDateTime sourceGceUpdatedAt,
                         Collection<DecisionTableElement> elements,
                         Collection<DecisionTableCell> cells) {
        this(id, gceId, projectId, name, description, sourceFingerprint, syncStatus, sourceGceUpdatedAt,
                elements, cells, null, null);
    }

    public DecisionTable(UUID id,
                         UUID gceId,
                         UUID projectId,
                         String name,
                         String description,
                         String sourceFingerprint,
                         DecisionTableSyncStatusEnum syncStatus,
                         LocalDateTime sourceGceUpdatedAt,
                         Collection<DecisionTableElement> elements,
                         Collection<DecisionTableCell> cells,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.gceId = gceId;
        this.projectId = requireUuid(projectId, "projectId");
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
        this.sourceFingerprint = requireText(sourceFingerprint, "sourceFingerprint");
        this.syncStatus = Objects.requireNonNull(syncStatus, "syncStatus e obrigatorio.");
        this.sourceGceUpdatedAt = sourceGceUpdatedAt;
        this.elements = toList(elements, "elements");
        this.cells = toList(cells, "cells");

        validateAggregate();
    }

    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value;
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value.trim();
    }

    private String normalizeDescription(String value) {
        return value == null ? "" : value.trim();
    }

    private <T> List<T> toList(Collection<T> values, String field) {
        if (values == null) {
            return new ArrayList<>();
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(field + " nao pode conter valores nulos.");
        }
        return new ArrayList<>(values);
    }

    private void validateAggregate() {
        validateElementUniqueness(DecisionTableElementEnum.CONDITION, "condition");
        validateElementUniqueness(DecisionTableElementEnum.ACTION, "action");
        validateElementUniqueness(DecisionTableElementEnum.RULE, "rule");
        validateCells();
    }

    private void validateElementUniqueness(DecisionTableElementEnum type, String label) {
        List<DecisionTableElement> typedElements = elementsByType(type);
        ensureUniqueValues(typedElements.stream().map(DecisionTableElement::getCode).toList(), label + " code duplicado.");
        ensureUniqueValues(typedElements.stream().map(DecisionTableElement::getOrderIndex).toList(), label + " orderIndex duplicado.");
    }

    private <T> void ensureUniqueValues(List<T> values, String message) {
        Set<T> uniques = new HashSet<>();
        for (T value : values) {
            if (!uniques.add(value)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    private void validateCells() {
        Map<UUID, DecisionTableElement> elementsById = elements.stream()
                .filter(element -> element.getId() != null)
                .collect(Collectors.toMap(DecisionTableElement::getId, element -> element, (left, right) -> left, HashMap::new));
        Set<String> intersections = new HashSet<>();

        for (DecisionTableCell cell : cells) {
            if (cell.getType() == DecisionTableElementEnum.RULE) {
                throw new IllegalArgumentException("Decision table cell nao pode ter tipo RULE.");
            }

            DecisionTableElement ruleElement = elementsById.get(cell.getRuleElementId());
            if (!elementsById.isEmpty() && ruleElement == null) {
                throw new IllegalArgumentException("Decision table cell referencia ruleElementId inexistente no agregado.");
            }
            if (ruleElement != null && ruleElement.getType() != DecisionTableElementEnum.RULE) {
                throw new IllegalArgumentException("Decision table cell referencia ruleElementId que nao e RULE.");
            }

            DecisionTableElement element = elementsById.get(cell.getDecisionTableElementId());
            if (!elementsById.isEmpty() && element == null) {
                throw new IllegalArgumentException("Decision table cell referencia decisionTableElementId inexistente no agregado.");
            }
            if (element != null && element.getType() != cell.getType()) {
                throw new IllegalArgumentException("Decision table cell referencia elemento de tipo incompativel.");
            }

            String key = cell.getRuleElementId() + ":" + cell.getDecisionTableElementId() + ":" + cell.getType();
            if (!intersections.add(key)) {
                throw new IllegalArgumentException("Ja existe decision table cell para a mesma intersecao de regra e elemento.");
            }
        }
    }

    private List<DecisionTableElement> elementsByType(DecisionTableElementEnum type) {
        return elements.stream()
                .filter(element -> element.getType() == type)
                .toList();
    }

    public boolean isStale() {
        return syncStatus == DecisionTableSyncStatusEnum.STALE;
    }

    public void markAsStale() {
        this.syncStatus = DecisionTableSyncStatusEnum.STALE;
    }

    public void markAsSynchronized(String sourceFingerprint, LocalDateTime sourceGceUpdatedAt) {
        this.sourceFingerprint = requireText(sourceFingerprint, "sourceFingerprint");
        this.sourceGceUpdatedAt = sourceGceUpdatedAt;
        this.syncStatus = DecisionTableSyncStatusEnum.UP_TO_DATE;
    }

    public void clearGceReference() {
        this.gceId = null;
        this.markAsStale();
    }

    public void updateDetails(String name, String description) {
        this.name = requireText(name, "name");
        this.description = normalizeDescription(description);
    }

    public DecisionTableCell findConditionCell(UUID ruleElementId, UUID conditionElementId) {
        return findCell(ruleElementId, conditionElementId, DecisionTableElementEnum.CONDITION);
    }

    public DecisionTableCell findActionCell(UUID ruleElementId, UUID actionElementId) {
        return findCell(ruleElementId, actionElementId, DecisionTableElementEnum.ACTION);
    }

    private DecisionTableCell findCell(UUID ruleElementId, UUID elementId, DecisionTableElementEnum type) {
        return cells.stream()
                .filter(cell -> cell.getType() == type)
                .filter(cell -> cell.getRuleElementId().equals(ruleElementId) && cell.getDecisionTableElementId().equals(elementId))
                .findFirst()
                .orElse(null);
    }

    public UUID getGceId() {
        return gceId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceFingerprint() {
        return sourceFingerprint;
    }

    public DecisionTableSyncStatusEnum getSyncStatus() {
        return syncStatus;
    }

    public LocalDateTime getSourceGceUpdatedAt() {
        return sourceGceUpdatedAt;
    }

    public List<DecisionTableElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public List<DecisionTableElement> getConditionElements() {
        return elementsByType(DecisionTableElementEnum.CONDITION);
    }

    public List<DecisionTableElement> getActionElements() {
        return elementsByType(DecisionTableElementEnum.ACTION);
    }

    public List<DecisionTableElement> getRuleElements() {
        return elementsByType(DecisionTableElementEnum.RULE);
    }

    public List<DecisionTableCell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    public List<DecisionTableCell> getConditionCells() {
        return cells.stream()
                .filter(cell -> cell.getType() == DecisionTableElementEnum.CONDITION)
                .toList();
    }

    public List<DecisionTableCell> getActionCells() {
        return cells.stream()
                .filter(cell -> cell.getType() == DecisionTableElementEnum.ACTION)
                .toList();
    }
}
