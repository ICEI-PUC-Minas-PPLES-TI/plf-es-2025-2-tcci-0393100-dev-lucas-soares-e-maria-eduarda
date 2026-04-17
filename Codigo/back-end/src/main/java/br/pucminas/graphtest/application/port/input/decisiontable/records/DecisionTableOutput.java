package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Saida com a representacao completa de uma tabela de decisao.
 */
public record DecisionTableOutput(
        UUID id,
        UUID gceId,
        UUID projectId,
        String name,
        String description,
        String sourceFingerprint,
        DecisionTableSyncStatusEnum syncStatus,
        LocalDateTime sourceGceUpdatedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<DecisionTableConditionOutput> conditions,
        List<DecisionTableActionOutput> actions,
        List<DecisionTableRuleOutput> rules,
        List<DecisionTableConditionCellOutput> conditionCells,
        List<DecisionTableActionCellOutput> actionCells
) {

    public static DecisionTableOutput from(DecisionTable decisionTable) {
        return new DecisionTableOutput(
                decisionTable.getId(),
                decisionTable.getGceId(),
                decisionTable.getProjectId(),
                decisionTable.getName(),
                decisionTable.getDescription(),
                decisionTable.getSourceFingerprint(),
                decisionTable.getSyncStatus(),
                decisionTable.getSourceGceUpdatedAt(),
                decisionTable.getCreatedAt(),
                normalizeUpdatedAt(decisionTable.getCreatedAt(), decisionTable.getUpdatedAt()),
                decisionTable.getConditions().stream().map(DecisionTableConditionOutput::from).toList(),
                decisionTable.getActions().stream().map(DecisionTableActionOutput::from).toList(),
                decisionTable.getRules().stream().map(DecisionTableRuleOutput::from).toList(),
                decisionTable.getConditionCells().stream().map(DecisionTableConditionCellOutput::from).toList(),
                decisionTable.getActionCells().stream().map(DecisionTableActionCellOutput::from).toList()
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
