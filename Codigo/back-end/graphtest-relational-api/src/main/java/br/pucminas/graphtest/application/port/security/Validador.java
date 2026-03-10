package br.pucminas.graphtest.application.port.security;

import br.pucminas.graphtest.adapters.outbound.security.UserDetailsImpl;

import java.util.UUID;

/**
 * Realiza as validações para ver se um usuário pode realizar uma determinada requisição
 */
public interface Validador {

    boolean validar(UUID id, UserDetailsImpl userDetailsImpl);

    String getTopico();
}
