package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByIdInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;

import static java.lang.String.format;

public class FindUserByIdUseCaseImpl implements FindUserByIdUseCasePort {

    private final UserRepositoryPort userRepository;
    private final UserAuthorizationService userAuthorizationService;

    public FindUserByIdUseCaseImpl(
            UserRepositoryPort userRepository,
            UserAuthorizationService userAuthorizationService
    ) {
        this.userRepository = userRepository;
        this.userAuthorizationService = userAuthorizationService;
    }

    @Override
    public UserOutput execute(FindUserByIdInput input) {
        userAuthorizationService.authorizeForUser(input.id());
        return userRepository.findById(input.id())
                .map(UserOutput::from)
                .orElseThrow(() -> new EntityNotFoundException(format("usuario nao encontrado, id: %s", input.id())));
    }
}
