package br.pucminas.graphtest.application.port.input.user.records;

public record CreateUserInput(
        String name,
        String email,
        String password
) {}
