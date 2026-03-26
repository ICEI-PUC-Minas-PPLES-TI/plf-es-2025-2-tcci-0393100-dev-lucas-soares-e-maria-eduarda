package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.port.input.user.ListUsersUseCase;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;

import java.util.List;

public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;
    private final UserAuthorizationService userAuthorizationService;

    public ListUsersUseCaseImpl(
            UserRepository userRepository,
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
