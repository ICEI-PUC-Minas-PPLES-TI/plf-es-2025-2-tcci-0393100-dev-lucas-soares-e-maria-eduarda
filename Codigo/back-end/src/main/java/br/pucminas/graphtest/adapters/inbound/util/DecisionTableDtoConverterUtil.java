package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.DecisionTableDTO;
import br.pucminas.graphtest.adapters.inbound.dto.decisiontable.UpdateDecisionTableDetailsDTO;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableActionCellOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableActionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableConditionCellOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableConditionOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableRuleOutput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.GenerateDecisionTableInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.ListDecisionTablesByProjectInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.UpdateDecisionTableDetailsInput;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static br.pucminas.graphtest.shared.LogTopicsUtil.CONVERSOR_ENTIDADE_DTO_UTIL;
import static java.lang.String.format;

@UtilityClass
@Slf4j(topic = CONVERSOR_ENTIDADE_DTO_UTIL)
public class DecisionTableDtoConverterUtil {

    public static DecisionTableDTO toDto(@NotNull DecisionTableOutput output) {
        log.info(format(">>> toDto: convertendo DecisionTableOutput (id: %s) para DTO", output.id()));

        return DecisionTableDTO.builder()
                .id(output.id())
                .gceId(output.gceId())
                .projectId(output.projectId())
                .name(output.name())
                .description(output.description())
                .sourceFingerprint(output.sourceFingerprint())
                .syncStatus(output.syncStatus())
                .sourceGceUpdatedAt(output.sourceGceUpdatedAt())
                .createdAt(output.createdAt())
                .updatedAt(output.updatedAt())
                .conditions(output.conditions().stream().map(DecisionTableDtoConverterUtil::toConditionDto).toList())
                .actions(output.actions().stream().map(DecisionTableDtoConverterUtil::toActionDto).toList())
                .rules(output.rules().stream().map(DecisionTableDtoConverterUtil::toRuleDto).toList())
                .conditionCells(output.conditionCells().stream().map(DecisionTableDtoConverterUtil::toConditionCellDto).toList())
                .actionCells(output.actionCells().stream().map(DecisionTableDtoConverterUtil::toActionCellDto).toList())
                .build();
    }

    public static GenerateDecisionTableInput toGenerateInput(UUID gceId) {
        return new GenerateDecisionTableInput(gceId);
    }

    public static DecisionTableByGceIdInput toByGceIdInput(UUID gceId) {
        return new DecisionTableByGceIdInput(gceId);
    }

    public static DecisionTableByIdInput toByIdInput(UUID id) {
        return new DecisionTableByIdInput(id);
    }

    public static ListDecisionTablesByProjectInput toListByProjectInput(UUID projectId) {
        return new ListDecisionTablesByProjectInput(projectId);
    }

    public static UpdateDecisionTableDetailsInput toUpdateDetailsInput(UUID id, UpdateDecisionTableDetailsDTO dto) {
        return new UpdateDecisionTableDetailsInput(id, dto.name(), dto.description());
    }

    private static DecisionTableDTO.DecisionTableConditionDTO toConditionDto(DecisionTableConditionOutput output) {
        return new DecisionTableDTO.DecisionTableConditionDTO(
                output.id(),
                output.decisionTableId(),
                output.code(),
                output.label(),
                output.orderIndex(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    private static DecisionTableDTO.DecisionTableActionDTO toActionDto(DecisionTableActionOutput output) {
        return new DecisionTableDTO.DecisionTableActionDTO(
                output.id(),
                output.decisionTableId(),
                output.code(),
                output.label(),
                output.orderIndex(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    private static DecisionTableDTO.DecisionTableRuleDTO toRuleDto(DecisionTableRuleOutput output) {
        return new DecisionTableDTO.DecisionTableRuleDTO(
                output.id(),
                output.decisionTableId(),
                output.code(),
                output.description(),
                output.orderIndex(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    private static DecisionTableDTO.DecisionTableConditionCellDTO toConditionCellDto(DecisionTableConditionCellOutput output) {
        return new DecisionTableDTO.DecisionTableConditionCellDTO(
                output.id(),
                output.ruleId(),
                output.conditionId(),
                output.value(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    private static DecisionTableDTO.DecisionTableActionCellDTO toActionCellDto(DecisionTableActionCellOutput output) {
        return new DecisionTableDTO.DecisionTableActionCellDTO(
                output.id(),
                output.ruleId(),
                output.actionId(),
                output.value(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
