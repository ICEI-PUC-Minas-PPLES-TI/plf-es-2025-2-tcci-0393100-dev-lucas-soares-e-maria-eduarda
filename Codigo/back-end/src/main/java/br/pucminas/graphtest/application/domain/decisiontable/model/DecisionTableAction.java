package br.pucminas.graphtest.application.domain.decisiontable.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma linha de acao/efeito da tabela de decisao.
 */
public class DecisionTableAction extends BaseEntity {

    private UUID decisionTableId;
    private String code;
    private String label;
    private int orderIndex;

    public DecisionTableAction(UUID id, UUID decisionTableId, String code, String label, int orderIndex) {
        this(id, decisionTableId, code, label, orderIndex, null, null);
    }

    public DecisionTableAction(UUID id,
                               UUID decisionTableId,
                               String code,
                               String label,
                               int orderIndex,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.decisionTableId = requireUuid(decisionTableId, "decisionTableId");
        this.code = requireText(code, "code");
        this.label = requireText(label, "label");
        this.orderIndex = requireNonNegative(orderIndex, "orderIndex");
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

    public int getOrderIndex() {
        return orderIndex;
    }
}
