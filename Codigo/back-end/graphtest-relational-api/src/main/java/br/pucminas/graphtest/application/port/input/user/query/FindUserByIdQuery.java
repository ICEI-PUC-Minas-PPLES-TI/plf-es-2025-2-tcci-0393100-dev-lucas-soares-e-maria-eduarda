package br.pucminas.graphtest.application.port.input.user.query;

import java.util.UUID;

/**
 * Consulta utilizada para solicitar a busca de um usuario pelo seu identificador.
 *
 * @param id identificador unico do usuario a ser localizado
 */
public record FindUserByIdQuery(UUID id) {}
