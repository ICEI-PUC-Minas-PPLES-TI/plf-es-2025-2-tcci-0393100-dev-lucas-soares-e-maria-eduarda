package br.pucminas.graphtest.application.port.input.security.records;

import br.pucminas.graphtest.application.domain.UserProfileEnum;

import java.util.UUID;

public record GenerateTokenInput(
        UUID userId,
        String email,
        UserProfileEnum profile
) {}
