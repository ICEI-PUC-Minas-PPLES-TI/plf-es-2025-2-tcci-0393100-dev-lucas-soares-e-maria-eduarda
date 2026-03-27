package br.pucminas.graphtest.application.port.input.security.records;

import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;

import java.util.UUID;

public record AuthenticationUserResult(
        UUID id,
        String email,
        String password,
        UserProfileEnum profile
) {
    public static AuthenticationUserResult from(User user) {
        return new AuthenticationUserResult(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getProfile()
        );
    }
}
