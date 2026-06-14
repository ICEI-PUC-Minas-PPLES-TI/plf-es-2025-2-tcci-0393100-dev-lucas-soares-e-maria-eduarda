package br.pucminas.graphtest.application.port.input.user.records;

import br.pucminas.graphtest.application.domain.user.model.User;

import java.time.LocalDateTime;
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
        Integer profileCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserOutput from(User user) {
        return new UserOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getCodigo() : null,
                user.getCreatedAt(),
                normalizeUpdatedAt(user.getCreatedAt(), user.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
