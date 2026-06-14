package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma celula ligada a uma regra e a um elemento de condicao ou acao.
 */
public class DecisionTableCell extends BaseEntity {

    private UUID ruleElementId;
    private UUID decisionTableElementId;
    private DecisionTableElementEnum type;
    private DecisionTableCellValueEnum value;

    public DecisionTableCell(UUID id,
                             UUID ruleElementId,
                             UUID decisionTableElementId,
                             DecisionTableElementEnum type,
                             DecisionTableCellValueEnum value) {
        this(id, ruleElementId, decisionTableElementId, type, value, null, null);
    }

    public DecisionTableCell(UUID id,
                             UUID ruleElementId,
                             UUID decisionTableElementId,
                             DecisionTableElementEnum type,
                             DecisionTableCellValueEnum value,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ruleElementId = requireUuid(ruleElementId, "ruleElementId");
        this.decisionTableElementId = requireUuid(decisionTableElementId, "decisionTableElementId");
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");
        this.value = Objects.requireNonNull(value, "value e obrigatorio.");
        validateValueForType();
    }

    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value;
    }

    private void validateValueForType() {
        if (type == DecisionTableElementEnum.RULE) {
            throw new IllegalArgumentException("Celula nao pode ter tipo RULE.");
        }
        if (type == DecisionTableElementEnum.ACTION && value == DecisionTableCellValueEnum.IRRELEVANT) {
            throw new IllegalArgumentException("IRRELEVANT nao e valido para celula de acao.");
        }
    }

    public UUID getRuleElementId() {
        return ruleElementId;
    }

    public UUID getRuleId() {
        return ruleElementId;
    }

    public UUID getDecisionTableElementId() {
        return decisionTableElementId;
    }

    public DecisionTableElementEnum getType() {
        return type;
    }

    public DecisionTableCellValueEnum getValue() {
        return value;
    }
}
