package br.pucminas.graphtest.application.port.input.user.command;

import java.util.UUID;

public record UpdateUserCommand(
        UUID id,
        String name,
        String email,
        Integer profileCode
) {}
