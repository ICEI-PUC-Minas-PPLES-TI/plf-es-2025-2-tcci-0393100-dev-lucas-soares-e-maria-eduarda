package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class DeleteUserUseCaseImpl implements DeleteUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final UserAuthorizationService userAuthorizationService;
    private final ProjectRepositoryPort projectRepository;
    private final GceRepositoryPort gceRepository;

    public DeleteUserUseCaseImpl(UserRepositoryPort userRepositoryPort,
                                 UserAuthorizationService userAuthorizationService,
                                 ProjectRepositoryPort projectRepository,
                                 GceRepositoryPort gceRepository) {
        this.userRepository = userRepositoryPort;
        this.userAuthorizationService = userAuthorizationService;
        this.projectRepository = projectRepository;
        this.gceRepository = gceRepository;
    }

    @Override
    @Transactional
    public void execute(DeleteUserInput input) {
        userAuthorizationService.authorizeForUser(input.id());

        User user = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        List<Project> projects = projectRepository.findAllByUserId(input.id());
        for (Project project : projects) {
            gceRepository.deleteAllByProjectId(project.getId());
        }

        projectRepository.deleteAllByUserId(input.id());
        userRepository.deleteById(user.getId());
    }
}
