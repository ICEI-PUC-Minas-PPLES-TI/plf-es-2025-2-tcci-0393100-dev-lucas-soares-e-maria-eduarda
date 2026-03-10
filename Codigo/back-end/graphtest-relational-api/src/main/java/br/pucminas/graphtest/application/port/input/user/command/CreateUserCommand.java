package br.pucminas.graphtest.application.port.input.user.command;

public record CreateUserCommand(
        String name,
        String email,
        String password
) {}
