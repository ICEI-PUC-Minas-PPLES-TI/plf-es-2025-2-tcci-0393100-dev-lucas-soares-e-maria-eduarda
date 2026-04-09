package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

public class DeleteUserUseCaseImpl implements DeleteUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final UserAuthorizationService userAuthorizationService;
    private final ProjectRepositoryPort projectRepository;

    public DeleteUserUseCaseImpl(UserRepositoryPort userRepositoryPort,
                                 UserAuthorizationService userAuthorizationService,
                                 ProjectRepositoryPort projectRepository) {
        this.userRepository = userRepositoryPort;
        this.userAuthorizationService = userAuthorizationService;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public void execute(DeleteUserInput input) {
        userAuthorizationService.authorizeForUser(input.id());

        User user = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        projectRepository.deleteAllByUserId(input.id());
        userRepository.deleteById(user.getId());
    }
}
