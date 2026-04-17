package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableActionValueEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma celula de acao em uma regra da tabela de decisao.
 */
public class DecisionTableActionCell extends BaseEntity {

    private UUID ruleId;
    private UUID actionId;
    private DecisionTableActionValueEnum value;

    public DecisionTableActionCell(UUID id, UUID ruleId, UUID actionId, DecisionTableActionValueEnum value) {
        this(id, ruleId, actionId, value, null, null);
    }

    public DecisionTableActionCell(UUID id,
                                   UUID ruleId,
                                   UUID actionId,
                                   DecisionTableActionValueEnum value,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ruleId = requireUuid(ruleId, "ruleId");
        this.actionId = requireUuid(actionId, "actionId");
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

    public UUID getActionId() {
        return actionId;
    }

    public DecisionTableActionValueEnum getValue() {
        return value;
    }
}
