package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.dto.UserDTO;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static br.pucminas.graphtest.shared.logging.LogTopics.CONVERSOR_ENTIDADE_DTO_UTIL;
import static java.lang.String.format;

@UtilityClass
@Slf4j(topic = CONVERSOR_ENTIDADE_DTO_UTIL)
public class EntityDtoConverterUtil {

    public static UserDTO converterParaDTO(@NotNull UserResult usuario) {
        log.info(format(">>> converterParaDTO: convertendo UsuarioResult (id: %s) para DTO", usuario.id()));
        return UserDTO.builder()
                .id(usuario.id())
                .perfilUsuario(usuario.profileCode())
                .nome(usuario.name())
                .email(usuario.email())
                .senha(null)
                .build();
    }
}
