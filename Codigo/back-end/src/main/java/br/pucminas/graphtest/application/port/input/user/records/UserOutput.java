package br.pucminas.graphtest.application.port.input.user.records;

import br.pucminas.graphtest.application.domain.user.model.User;

import java.util.UUID;

/**
 * Saida com os dados de um usuario retornados pela camada de aplicacao.
 *
 * @param id identificador unico do usuario
 * @param name nome do usuario
 * @param email email do usuario
 * @param profileCode codigo do perfil associado ao usuario
 */
public record UserOutput(
        UUID id,
        String name,
        String email,
        Integer profileCode
) {
    public static UserOutput from(User user) {
        return new UserOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getCodigo() : null
        );
    }
}
