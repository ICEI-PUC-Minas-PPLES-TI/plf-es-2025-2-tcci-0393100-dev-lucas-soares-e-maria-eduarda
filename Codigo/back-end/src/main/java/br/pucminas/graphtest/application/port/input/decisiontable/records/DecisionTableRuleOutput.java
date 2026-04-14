package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableRule;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida com a representacao de uma regra da tabela de decisao.
 */
public record DecisionTableRuleOutput(
        UUID id,
        UUID decisionTableId,
        String code,
        String description,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DecisionTableRuleOutput from(DecisionTableRule rule) {
        return new DecisionTableRuleOutput(
                rule.getId(),
                rule.getDecisionTableId(),
                rule.getCode(),
                rule.getDescription(),
                rule.getOrderIndex(),
                rule.getCreatedAt(),
                normalizeUpdatedAt(rule.getCreatedAt(), rule.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
