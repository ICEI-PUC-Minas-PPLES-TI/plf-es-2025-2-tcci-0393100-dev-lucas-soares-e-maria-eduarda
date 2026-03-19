package br.pucminas.graphtest.application.port.input.user.command;

/**
 * Comando utilizado para transportar os dados necessarios para a criacao de um usuario.
 *
 * @param name nome do usuario a ser criado
 * @param email email do usuario a ser criado
 * @param password senha inicial do usuario a ser criado
 */
public record CreateUserCommand(
        String name,
        String email,
        String password
) {}
