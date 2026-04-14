package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Agregado raiz que representa uma tabela de decisao derivada de um GCE.
 *
 * <p>Apesar de derivada do GCE, a tabela possui ciclo de vida proprio e e
 * modelada como entidade relacional normalizada em regras, condicoes, acoes e
 * celulas.</p>
 */
public class DecisionTable extends BaseEntity {

    private UUID gceId;
    private UUID projectId;
    private String name;
    private String description;
    private String sourceFingerprint;
    private DecisionTableSyncStatusEnum syncStatus;
    private LocalDateTime generatedAt;
    private LocalDateTime sourceGceUpdatedAt;
    private final List<DecisionTableCondition> conditions;
    private final List<DecisionTableAction> actions;
    private final List<DecisionTableRule> rules;
    private final List<DecisionTableConditionCell> conditionCells;
    private final List<DecisionTableActionCell> actionCells;

    public DecisionTable(UUID id,
                         UUID gceId,
                         UUID projectId,
                         String name,
                         String description,
                         String sourceFingerprint,
                         DecisionTableSyncStatusEnum syncStatus,
                         LocalDateTime generatedAt,
                         LocalDateTime sourceGceUpdatedAt,
                         Collection<DecisionTableCondition> conditions,
                         Collection<DecisionTableAction> actions,
                         Collection<DecisionTableRule> rules,
                         Collection<DecisionTableConditionCell> conditionCells,
                         Collection<DecisionTableActionCell> actionCells) {
        this(
                id,
                gceId,
                projectId,
                name,
                description,
                sourceFingerprint,
                syncStatus,
                generatedAt,
                sourceGceUpdatedAt,
                conditions,
                actions,
                rules,
                conditionCells,
                actionCells,
                null,
                null
        );
    }

    public DecisionTable(UUID id,
                         UUID gceId,
                         UUID projectId,
                         String name,
                         String description,
                         String sourceFingerprint,
                         DecisionTableSyncStatusEnum syncStatus,
                         LocalDateTime generatedAt,
                         LocalDateTime sourceGceUpdatedAt,
                         Collection<DecisionTableCondition> conditions,
                         Collection<DecisionTableAction> actions,
                         Collection<DecisionTableRule> rules,
                         Collection<DecisionTableConditionCell> conditionCells,
                         Collection<DecisionTableActionCell> actionCells,
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
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt e obrigatorio.");
        this.sourceGceUpdatedAt = sourceGceUpdatedAt;
        this.conditions = toList(conditions, "conditions");
        this.actions = toList(actions, "actions");
        this.rules = toList(rules, "rules");
        this.conditionCells = toList(conditionCells, "conditionCells");
        this.actionCells = toList(actionCells, "actionCells");

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
        validateConditionUniqueness();
        validateActionUniqueness();
        validateRuleUniqueness();
        validateConditionCells();
        validateActionCells();
    }

    private void validateConditionUniqueness() {
        ensureUniqueCodesAndOrderIndexes(
                conditions.stream().map(DecisionTableCondition::getCode).toList(),
                conditions.stream().map(DecisionTableCondition::getOrderIndex).toList(),
                "condition"
        );
    }

    private void validateActionUniqueness() {
        ensureUniqueCodesAndOrderIndexes(
                actions.stream().map(DecisionTableAction::getCode).toList(),
                actions.stream().map(DecisionTableAction::getOrderIndex).toList(),
                "action"
        );
    }

    private void validateRuleUniqueness() {
        ensureUniqueCodesAndOrderIndexes(
                rules.stream().map(DecisionTableRule::getCode).toList(),
                rules.stream().map(DecisionTableRule::getOrderIndex).toList(),
                "rule"
        );
    }

    private void ensureUniqueCodesAndOrderIndexes(List<String> codes, List<Integer> orderIndexes, String type) {
        ensureUniqueValues(codes, type + " code duplicado.");
        ensureUniqueValues(orderIndexes, type + " orderIndex duplicado.");
    }

    private <T> void ensureUniqueValues(List<T> values, String message) {
        Set<T> uniques = new HashSet<>();
        for (T value : values) {
            if (!uniques.add(value)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    private void validateConditionCells() {
        Set<UUID> ruleIds = rules.stream().map(DecisionTableRule::getId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        Set<UUID> conditionIds = conditions.stream().map(DecisionTableCondition::getId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        Set<String> intersections = new HashSet<>();

        for (DecisionTableConditionCell cell : conditionCells) {
            if (!ruleIds.isEmpty() && !ruleIds.contains(cell.getRuleId())) {
                throw new IllegalArgumentException("Condition cell referencia ruleId inexistente no agregado.");
            }
            if (!conditionIds.isEmpty() && !conditionIds.contains(cell.getConditionId())) {
                throw new IllegalArgumentException("Condition cell referencia conditionId inexistente no agregado.");
            }

            String key = cell.getRuleId() + ":" + cell.getConditionId();
            if (!intersections.add(key)) {
                throw new IllegalArgumentException("Ja existe condition cell para a mesma intersecao de regra e condicao.");
            }
        }
    }

    private void validateActionCells() {
        Set<UUID> ruleIds = rules.stream().map(DecisionTableRule::getId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        Set<UUID> actionIds = actions.stream().map(DecisionTableAction::getId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        Set<String> intersections = new HashSet<>();

        for (DecisionTableActionCell cell : actionCells) {
            if (!ruleIds.isEmpty() && !ruleIds.contains(cell.getRuleId())) {
                throw new IllegalArgumentException("Action cell referencia ruleId inexistente no agregado.");
            }
            if (!actionIds.isEmpty() && !actionIds.contains(cell.getActionId())) {
                throw new IllegalArgumentException("Action cell referencia actionId inexistente no agregado.");
            }

            String key = cell.getRuleId() + ":" + cell.getActionId();
            if (!intersections.add(key)) {
                throw new IllegalArgumentException("Ja existe action cell para a mesma intersecao de regra e acao.");
            }
        }
    }

    public boolean isStale() {
        return syncStatus == DecisionTableSyncStatusEnum.STALE;
    }

    public void markAsStale() {
        this.syncStatus = DecisionTableSyncStatusEnum.STALE;
    }

    public void markAsSynchronized(String sourceFingerprint, LocalDateTime generatedAt, LocalDateTime sourceGceUpdatedAt) {
        this.sourceFingerprint = requireText(sourceFingerprint, "sourceFingerprint");
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt e obrigatorio.");
        this.sourceGceUpdatedAt = sourceGceUpdatedAt;
        this.syncStatus = DecisionTableSyncStatusEnum.UP_TO_DATE;
    }

    public void clearGceReference() {
        this.gceId = null;
        this.markAsStale();
    }

    public DecisionTableConditionCell findConditionCell(UUID ruleId, UUID conditionId) {
        return conditionCells.stream()
                .filter(cell -> cell.getRuleId().equals(ruleId) && cell.getConditionId().equals(conditionId))
                .findFirst()
                .orElse(null);
    }

    public DecisionTableActionCell findActionCell(UUID ruleId, UUID actionId) {
        return actionCells.stream()
                .filter(cell -> cell.getRuleId().equals(ruleId) && cell.getActionId().equals(actionId))
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

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public LocalDateTime getSourceGceUpdatedAt() {
        return sourceGceUpdatedAt;
    }

    public List<DecisionTableCondition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public List<DecisionTableAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public List<DecisionTableRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public List<DecisionTableConditionCell> getConditionCells() {
        return Collections.unmodifiableList(conditionCells);
    }

    public List<DecisionTableActionCell> getActionCells() {
        return Collections.unmodifiableList(actionCells);
    }
}
