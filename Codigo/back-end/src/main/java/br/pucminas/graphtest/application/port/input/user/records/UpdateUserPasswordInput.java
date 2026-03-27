package br.pucminas.graphtest.application.port.input.user.records;

import java.util.UUID;

public record UpdateUserPasswordInput(
        UUID id,
        String senhaOriginal,
        String senhaAtualizada
) {}
