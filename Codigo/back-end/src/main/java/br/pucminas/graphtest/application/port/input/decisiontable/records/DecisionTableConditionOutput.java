package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida com a representacao de uma condicao da tabela de decisao.
 */
public record DecisionTableConditionOutput(
        UUID id,
        UUID decisionTableId,
        String code,
        String label,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DecisionTableConditionOutput from(DecisionTableElement condition) {
        if (condition.getType() != DecisionTableElementEnum.CONDITION) {
            throw new IllegalArgumentException("Elemento nao e uma condicao.");
        }
        return new DecisionTableConditionOutput(
                condition.getId(),
                condition.getDecisionTableId(),
                condition.getCode(),
                condition.getLabel(),
                condition.getOrderIndex(),
                condition.getCreatedAt(),
                normalizeUpdatedAt(condition.getCreatedAt(), condition.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
