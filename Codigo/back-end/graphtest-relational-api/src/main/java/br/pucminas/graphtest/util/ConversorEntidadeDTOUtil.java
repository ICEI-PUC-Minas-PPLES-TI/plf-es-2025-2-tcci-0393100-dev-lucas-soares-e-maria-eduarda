package br.pucminas.graphtest.util;

import br.pucminas.graphtest.dto.UserDTO;
import br.pucminas.graphtest.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.CONVERSOR_ENTIDADE_DTO_UTIL;
import static java.lang.String.format;

@UtilityClass
@Slf4j(topic = CONVERSOR_ENTIDADE_DTO_UTIL)
public class ConversorEntidadeDTOUtil {

    /**
     * Converte uma entidade do tipo Usuario para UsuarioDTO
     *
     * @param usuario entidade do tipo Usuario
     * @return novo UsuarioDTO
     */
    public static UserDTO converterParaDTO(@NotNull User usuario) {
        log.info(format(">>> converterParaDTO: convertendo Usuario (id: %s) para DTO", usuario.getId()));
        return UserDTO.builder()
                .id(usuario.getId())
                .perfilUsuario(usuario.getPerfilUsuario())
                .nome(usuario.getName())
                .email(usuario.getEmail())
                .build();
    }
}
