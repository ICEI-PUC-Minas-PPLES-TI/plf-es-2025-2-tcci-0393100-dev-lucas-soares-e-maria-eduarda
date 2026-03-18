package br.pucminas.graphtest.application.port.input.security.command;

import br.pucminas.graphtest.application.domain.model.UserProfileEnum;

import java.util.UUID;

public record GenerateTokenCommand(
        UUID userId,
        String email,
        UserProfileEnum profile
) {
}
