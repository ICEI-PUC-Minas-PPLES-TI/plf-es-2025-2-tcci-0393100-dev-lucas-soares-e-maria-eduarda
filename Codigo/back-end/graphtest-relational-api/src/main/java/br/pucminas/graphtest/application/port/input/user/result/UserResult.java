package br.pucminas.graphtest.application.port.input.user.result;

import br.pucminas.graphtest.application.domain.model.User;
import java.util.UUID;

/**
 * Resultado de saida com os dados de um usuario retornados pela camada de aplicacao.
 *
 * @param id identificador unico do usuario
 * @param name nome do usuario
 * @param email email do usuario
 * @param profileCode codigo do perfil associado ao usuario
 */
public record UserResult(
        UUID id,
        String name,
        String email,
        Integer profileCode
) {
    /**
     * Converte uma entidade de dominio {@link User} em um {@code UserResult}.
     *
     * @param user entidade de usuario a ser convertida
     * @return representacao imutavel dos dados do usuario para saida da aplicacao
     */
    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getCodigo() : null
        );
    }
}
