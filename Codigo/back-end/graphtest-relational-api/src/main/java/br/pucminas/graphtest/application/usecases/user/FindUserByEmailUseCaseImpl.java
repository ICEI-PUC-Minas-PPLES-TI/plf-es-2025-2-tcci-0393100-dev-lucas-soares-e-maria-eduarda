package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.FindUserByEmailUseCase;
import br.pucminas.graphtest.application.port.input.user.query.FindUserByEmailQuery;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class FindUserByEmailUseCaseImpl implements FindUserByEmailUseCase {

    private final UserRepository userRepository;

    public FindUserByEmailUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResult execute(FindUserByEmailQuery query) {
        return userRepository.findByEmail(query.email())
                .map(UserResult::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("usuario nao encontrado, email: %s", query.email())
                ));
    }
}
