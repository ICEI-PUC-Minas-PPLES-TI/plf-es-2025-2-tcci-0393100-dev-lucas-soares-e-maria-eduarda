package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableElement;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida com a representacao de uma acao da tabela de decisao.
 */
public record DecisionTableActionOutput(
        UUID id,
        UUID decisionTableId,
        String code,
        String label,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DecisionTableActionOutput from(DecisionTableElement action) {
        if (action.getType() != DecisionTableElementEnum.ACTION) {
            throw new IllegalArgumentException("Elemento nao e uma acao.");
        }
        return new DecisionTableActionOutput(
                action.getId(),
                action.getDecisionTableId(),
                action.getCode(),
                action.getLabel(),
                action.getOrderIndex(),
                action.getCreatedAt(),
                normalizeUpdatedAt(action.getCreatedAt(), action.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
