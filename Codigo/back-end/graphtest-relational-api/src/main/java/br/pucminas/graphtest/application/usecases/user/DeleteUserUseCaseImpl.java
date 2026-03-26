package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCase;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;
    private final UserAuthorizationService userAuthorizationService;
    private final ProjectRepository projectRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository,
                                 UserAuthorizationService userAuthorizationService,
                                 ProjectRepository projectRepository) {
        this.userRepository = userRepository;
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
