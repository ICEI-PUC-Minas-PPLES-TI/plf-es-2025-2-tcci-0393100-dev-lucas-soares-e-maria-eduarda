package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.security.AuthenticatedUser;

public interface AuthenticatedUserByTokenUseCasePort {

    /**
     * @return usuario autenticado ou null quando o token nao puder ser resolvido
     */
    AuthenticatedUser execute(String token);
}
