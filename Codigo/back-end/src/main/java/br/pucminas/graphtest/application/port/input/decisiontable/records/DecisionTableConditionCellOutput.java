package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableElementEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTableCell;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida com a representacao de uma celula de condicao da tabela de decisao.
 */
public record DecisionTableConditionCellOutput(
        UUID id,
        UUID ruleId,
        UUID conditionId,
        DecisionTableCellValueEnum value,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DecisionTableConditionCellOutput from(DecisionTableCell cell) {
        if (cell.getType() != DecisionTableElementEnum.CONDITION) {
            throw new IllegalArgumentException("Celula nao e de condicao.");
        }
        return new DecisionTableConditionCellOutput(
                cell.getId(),
                cell.getRuleId(),
                cell.getDecisionTableElementId(),
                cell.getValue(),
                cell.getCreatedAt(),
                normalizeUpdatedAt(cell.getCreatedAt(), cell.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
