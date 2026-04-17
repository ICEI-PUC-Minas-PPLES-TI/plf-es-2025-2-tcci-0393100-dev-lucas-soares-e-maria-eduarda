package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableConditionCell;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida com a representacao de uma celula de condicao da tabela de decisao.
 */
public record DecisionTableConditionCellOutput(
        UUID id,
        UUID ruleId,
        UUID conditionId,
        DecisionTableConditionValueEnum value,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DecisionTableConditionCellOutput from(DecisionTableConditionCell cell) {
        return new DecisionTableConditionCellOutput(
                cell.getId(),
                cell.getRuleId(),
                cell.getConditionId(),
                cell.getValue(),
                cell.getCreatedAt(),
                normalizeUpdatedAt(cell.getCreatedAt(), cell.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
