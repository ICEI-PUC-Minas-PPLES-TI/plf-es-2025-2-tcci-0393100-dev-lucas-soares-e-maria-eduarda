package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableActionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableActionCell;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida com a representacao de uma celula de acao da tabela de decisao.
 */
public record DecisionTableActionCellOutput(
        UUID id,
        UUID ruleId,
        UUID actionId,
        DecisionTableActionValueEnum value,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DecisionTableActionCellOutput from(DecisionTableActionCell cell) {
        return new DecisionTableActionCellOutput(
                cell.getId(),
                cell.getRuleId(),
                cell.getActionId(),
                cell.getValue(),
                cell.getCreatedAt(),
                normalizeUpdatedAt(cell.getCreatedAt(), cell.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
