package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.port.input.security.records.LoadAuthenticationUserInput;
import br.pucminas.graphtest.application.port.input.security.records.AuthenticationUserResult;

public interface LoadAuthenticationUserUseCasePort {

    AuthenticationUserResult execute(LoadAuthenticationUserInput input);
}
