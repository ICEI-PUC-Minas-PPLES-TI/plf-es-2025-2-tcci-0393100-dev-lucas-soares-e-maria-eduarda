package br.pucminas.graphtest.application.port.input.user.result;

import br.pucminas.graphtest.application.domain.model.User;

import java.util.UUID;

public record UserResult(
        UUID id,
        String name,
        String email,
        Integer profileCode
) {
    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getCodigo() : null
        );
    }
}
