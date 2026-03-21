package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserIsAdminUseCase;
import br.pucminas.graphtest.application.port.input.user.ListUsersUseCase;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;
    private final AuthorizeCurrentUserIsAdminUseCase authorizeCurrentUserIsAdminUseCase;

    public ListUsersUseCaseImpl(
            UserRepository userRepository,
            AuthorizeCurrentUserIsAdminUseCase authorizeCurrentUserIsAdminUseCase
    ) {
        this.userRepository = userRepository;
        this.authorizeCurrentUserIsAdminUseCase = authorizeCurrentUserIsAdminUseCase;
    }

    @Override
    public List<UserOutput> execute() {
        authorizeCurrentUserIsAdminUseCase.execute();
        return userRepository.findAll().stream()
                .map(UserOutput::from)
                .toList();
    }
}
