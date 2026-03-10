package br.pucminas.graphtest.application.port.input.user.command;

import java.util.UUID;

public record UpdateUserPasswordCommand(
        UUID id,
        String senhaOriginal,
        String senhaAtualizada
) {}