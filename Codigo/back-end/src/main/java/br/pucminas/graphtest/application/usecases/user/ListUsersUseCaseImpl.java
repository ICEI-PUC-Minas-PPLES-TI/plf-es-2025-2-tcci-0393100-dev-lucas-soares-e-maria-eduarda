package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.port.input.user.ListUsersUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;

import java.util.List;

public class ListUsersUseCaseImpl implements ListUsersUseCasePort {

    private final UserRepositoryPort userRepository;
    private final UserAuthorizationService userAuthorizationService;

    public ListUsersUseCaseImpl(
            UserRepositoryPort userRepository,
            UserAuthorizationService userAuthorizationService
    ) {
        this.userRepository = userRepository;
        this.userAuthorizationService = userAuthorizationService;
    }

    @Override
    public List<UserOutput> execute() {
        userAuthorizationService.authorizeAdmin();
        return userRepository.findAll().stream()
                .map(UserOutput::from)
                .toList();
    }
}
