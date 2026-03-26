package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.FindUserByEmailUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByEmailInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;

import static java.lang.String.format;

public class FindUserByEmailUseCaseImpl implements FindUserByEmailUseCasePort {

    private final UserRepositoryPort userRepository;

    public FindUserByEmailUseCaseImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserOutput execute(FindUserByEmailInput input) {
        return userRepository.findByEmail(input.email())
                .map(UserOutput::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("usuario nao encontrado, email: %s", input.email())
                ));
    }
}
