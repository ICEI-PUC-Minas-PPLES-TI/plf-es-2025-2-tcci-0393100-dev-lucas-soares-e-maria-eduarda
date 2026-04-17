package br.pucminas.graphtest.adapters.inbound.dto.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableActionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;
import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DecisionTableDTO(
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
        List<DecisionTableConditionDTO> conditions,
        List<DecisionTableActionDTO> actions,
        List<DecisionTableRuleDTO> rules,
        List<DecisionTableConditionCellDTO> conditionCells,
        List<DecisionTableActionCellDTO> actionCells
) {

    public record DecisionTableConditionDTO(
            UUID id,
            UUID decisionTableId,
            String code,
            String label,
            int orderIndex,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record DecisionTableActionDTO(
            UUID id,
            UUID decisionTableId,
            String code,
            String label,
            int orderIndex,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record DecisionTableRuleDTO(
            UUID id,
            UUID decisionTableId,
            String code,
            String description,
            int orderIndex,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record DecisionTableConditionCellDTO(
            UUID id,
            UUID ruleId,
            UUID conditionId,
            DecisionTableConditionValueEnum value,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record DecisionTableActionCellDTO(
            UUID id,
            UUID ruleId,
            UUID actionId,
            DecisionTableActionValueEnum value,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
