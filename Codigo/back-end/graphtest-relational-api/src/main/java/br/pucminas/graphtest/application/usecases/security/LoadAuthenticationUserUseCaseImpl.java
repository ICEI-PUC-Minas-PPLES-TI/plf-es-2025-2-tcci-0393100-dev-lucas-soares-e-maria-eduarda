package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.LoadAuthenticationUserUseCasePort;
import br.pucminas.graphtest.application.port.input.security.records.LoadAuthenticationUserInput;
import br.pucminas.graphtest.application.port.input.security.records.AuthenticationUserResult;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;

import static java.lang.String.format;

public class LoadAuthenticationUserUseCaseImpl implements LoadAuthenticationUserUseCasePort {

    private final UserRepositoryPort userRepository;

    public LoadAuthenticationUserUseCaseImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticationUserResult execute(LoadAuthenticationUserInput input) {
        return userRepository.findByEmail(input.email())
                .map(AuthenticationUserResult::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("usuario nao encontrado, email: %s", input.email())
                ));
    }
}
