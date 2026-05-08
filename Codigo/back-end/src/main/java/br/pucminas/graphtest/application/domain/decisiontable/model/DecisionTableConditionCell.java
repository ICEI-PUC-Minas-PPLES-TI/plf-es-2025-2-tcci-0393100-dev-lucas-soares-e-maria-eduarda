package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma celula de condicao em uma regra da tabela de decisao.
 */
public class DecisionTableConditionCell extends BaseEntity {

    private UUID ruleId;
    private UUID conditionId;
    private DecisionTableConditionValueEnum value;

    public DecisionTableConditionCell(UUID id, UUID ruleId, UUID conditionId, DecisionTableConditionValueEnum value) {
        this(id, ruleId, conditionId, value, null, null);
    }

    public DecisionTableConditionCell(UUID id,
                                      UUID ruleId,
                                      UUID conditionId,
                                      DecisionTableConditionValueEnum value,
                                      LocalDateTime createdAt,
                                      LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ruleId = requireUuid(ruleId, "ruleId");
        this.conditionId = requireUuid(conditionId, "conditionId");
        this.value = Objects.requireNonNull(value, "value e obrigatorio.");
    }

    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public UUID getConditionId() {
        return conditionId;
    }

    public DecisionTableConditionValueEnum getValue() {
        return value;
    }
}
