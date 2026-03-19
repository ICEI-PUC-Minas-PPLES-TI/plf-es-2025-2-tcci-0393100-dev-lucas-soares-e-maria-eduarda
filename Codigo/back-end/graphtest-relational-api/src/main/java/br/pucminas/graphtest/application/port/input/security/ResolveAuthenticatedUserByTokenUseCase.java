package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.domain.model.AuthenticatedUser;

public interface ResolveAuthenticatedUserByTokenUseCase {

    /**
     * @return usuario autenticado ou null quando o token nao puder ser resolvido
     */
    AuthenticatedUser execute(String token);
}
