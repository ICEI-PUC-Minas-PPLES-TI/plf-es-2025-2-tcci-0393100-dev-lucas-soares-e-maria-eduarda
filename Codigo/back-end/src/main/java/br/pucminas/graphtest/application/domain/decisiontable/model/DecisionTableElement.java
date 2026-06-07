package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma condicao, acao ou regra de uma tabela de decisao.
 */
public class DecisionTableElement extends BaseEntity {

    private UUID decisionTableId;
    private String code;
    private String label;
    private String description;
    private int orderIndex;
    private DecisionTableElementEnum type;

    public DecisionTableElement(UUID id,
                                UUID decisionTableId,
                                String code,
                                String label,
                                int orderIndex,
                                DecisionTableElementEnum type) {
        this(id, decisionTableId, code, label, null, orderIndex, type, null, null);
    }

    public DecisionTableElement(UUID id,
                                UUID decisionTableId,
                                String code,
                                String label,
                                int orderIndex,
                                DecisionTableElementEnum type,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        this(id, decisionTableId, code, label, null, orderIndex, type, createdAt, updatedAt);
    }

    public DecisionTableElement(UUID id,
                                UUID decisionTableId,
                                String code,
                                String label,
                                String description,
                                int orderIndex,
                                DecisionTableElementEnum type) {
        this(id, decisionTableId, code, label, description, orderIndex, type, null, null);
    }

    public DecisionTableElement(UUID id,
                                UUID decisionTableId,
                                String code,
                                String label,
                                String description,
                                int orderIndex,
                                DecisionTableElementEnum type,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.decisionTableId = requireUuid(decisionTableId, "decisionTableId");
        this.code = requireText(code, "code");
        this.label = normalize(label);
        this.description = normalizeDescription(description);
        this.orderIndex = requireNonNegative(orderIndex, "orderIndex");
        this.type = Objects.requireNonNull(type, "type e obrigatorio.");
        validateTypeContract();
    }

    private void validateTypeContract() {
        if (type == DecisionTableElementEnum.RULE) {
            if (description == null) {
                throw new IllegalArgumentException("description e obrigatorio para regra.");
            }
            if (label != null) {
                throw new IllegalArgumentException("label deve ser nulo para regra.");
            }
            return;
        }

        if (label == null) {
            throw new IllegalArgumentException("label e obrigatorio para condicao ou acao.");
        }
        if (description != null) {
            throw new IllegalArgumentException("description deve ser nulo para condicao ou acao.");
        }
    }

    private UUID requireUuid(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return value;
    }

    private String requireText(String value, String field) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException(field + " e obrigatorio.");
        }
        return normalized;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeDescription(String value) {
        return value == null ? null : value.trim();
    }

    private int requireNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " nao pode ser negativo.");
        }
        return value;
    }

    public UUID getDecisionTableId() {
        return decisionTableId;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public DecisionTableElementEnum getType() {
        return type;
    }
}
