package br.pucminas.graphtest.application.port.input.user.records;

import java.util.UUID;

public record UpdateUserInput(
        UUID id,
        String name,
        String email,
        Integer profileCode
) {}
