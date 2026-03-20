package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserForUserUseCase;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCase;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByIdInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class FindUserByIdUseCaseImpl implements FindUserByIdUseCase {

    private final UserRepository userRepository;
    private final AuthorizeCurrentUserForUserUseCase authorizeCurrentUserForUserUseCase;

    public FindUserByIdUseCaseImpl(
            UserRepository userRepository,
            AuthorizeCurrentUserForUserUseCase authorizeCurrentUserForUserUseCase
    ) {
        this.userRepository = userRepository;
        this.authorizeCurrentUserForUserUseCase = authorizeCurrentUserForUserUseCase;
    }

    @Override
    public UserOutput execute(FindUserByIdInput input) {
        authorizeCurrentUserForUserUseCase.execute(input.id());
        return userRepository.findById(input.id())
                .map(UserOutput::from)
                .orElseThrow(() -> new EntityNotFoundException(format("usuario nao encontrado, id: %s", input.id())));
    }
}
