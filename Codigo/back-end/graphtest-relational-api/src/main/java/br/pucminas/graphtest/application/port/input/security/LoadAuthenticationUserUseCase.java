package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.port.input.security.query.LoadAuthenticationUserQuery;
import br.pucminas.graphtest.application.port.input.security.result.AuthenticationUserResult;

public interface LoadAuthenticationUserUseCase {

    AuthenticationUserResult execute(LoadAuthenticationUserQuery query);
}
