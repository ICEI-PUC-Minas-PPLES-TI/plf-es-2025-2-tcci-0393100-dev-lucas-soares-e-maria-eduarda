package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.user.UserDTO;
import br.pucminas.graphtest.adapters.inbound.dto.project.ProjectDTO;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static br.pucminas.graphtest.shared.LogTopicsUtil.CONVERSOR_ENTIDADE_DTO_UTIL;
import static java.lang.String.format;

@UtilityClass
@Slf4j(topic = CONVERSOR_ENTIDADE_DTO_UTIL)
public class EntityDtoConverterUtil {

    public static UserDTO toDto(@NotNull UserOutput userOutput) {
        log.info(format(">>> toDto: convertendo UserOutput (id: %s) para DTO", userOutput.id()));
        return UserDTO.builder()
                .id(userOutput.id())
                .profileUser(userOutput.profileCode())
                .name(userOutput.name())
                .email(userOutput.email())
                .password(null)
                .build();
    }

    public static ProjectDTO toDto(@NotNull ProjectOutput projectOutput) {
        log.info(format(">>> toDto: convertendo ProjectOutput (id: %s) para DTO", projectOutput.id()));
        return ProjectDTO.builder()
                .id(projectOutput.id())
                .name(projectOutput.name())
                .description(projectOutput.description())
                .build();
    }
}
