package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.LoadAuthenticationUserUseCase;
import br.pucminas.graphtest.application.port.input.security.query.LoadAuthenticationUserQuery;
import br.pucminas.graphtest.application.port.input.security.result.AuthenticationUserResult;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class LoadAuthenticationUserUseCaseImpl implements LoadAuthenticationUserUseCase {

    private final UserRepository userRepository;

    public LoadAuthenticationUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticationUserResult execute(LoadAuthenticationUserQuery query) {
        return userRepository.findByEmail(query.email())
                .map(AuthenticationUserResult::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("usuario nao encontrado, email: %s", query.email())
                ));
    }
}
