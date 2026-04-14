package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

public class DeleteUserUseCaseImpl implements DeleteUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final UserAuthorizationService userAuthorizationService;
    private final ProjectDeletionService projectDeletionService;

    public DeleteUserUseCaseImpl(UserRepositoryPort userRepositoryPort,
                                 UserAuthorizationService userAuthorizationService,
                                 ProjectDeletionService projectDeletionService) {
        this.userRepository = userRepositoryPort;
        this.userAuthorizationService = userAuthorizationService;
        this.projectDeletionService = projectDeletionService;
    }

    @Override
    @Transactional
    public void execute(DeleteUserInput input) {
        userAuthorizationService.authorizeForUser(input.id());

        User user = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        projectDeletionService.deleteProjectsByUserId(input.id());
        userRepository.deleteById(user.getId());
    }
}
