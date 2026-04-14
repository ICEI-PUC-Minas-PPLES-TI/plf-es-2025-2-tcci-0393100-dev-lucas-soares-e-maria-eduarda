package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.security.AuthenticatedUser;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import br.pucminas.graphtest.application.service.user.interfaces.UserEmailUniquenessService;

import java.time.LocalDateTime;

public class UpdateUserUseCaseImpl implements UpdateUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final UserAuthorizationService userAuthorizationService;
    private final UserEmailUniquenessService userEmailUniquenessService;

    public UpdateUserUseCaseImpl(UserRepositoryPort userRepository,
                                 UserAuthorizationService userAuthorizationService,
                                 UserEmailUniquenessService userEmailUniquenessService) {
        this.userAuthorizationService = userAuthorizationService;
        this.userRepository = userRepository;
        this.userEmailUniquenessService = userEmailUniquenessService;

    }

    @Override
    public UserOutput execute(UpdateUserInput input) {
        AuthenticatedUser currentUser = userAuthorizationService.authorizeForUser(input.id());
        User user = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        userEmailUniquenessService.ensureEmailAvailableForUpdate(input.id(), input.email());

        user.setEmail(input.email());
        user.setName(input.name());
        updateProfileIfAllowed(input, currentUser, user);
        user.setUpdatedAt(LocalDateTime.now());

        return UserOutput.from(userRepository.save(user));
    }


    private void updateProfileIfAllowed(UpdateUserInput input, AuthenticatedUser currentUser, User user) {
        if (currentUser.isAdmin() && input.profileCode() != null) {
            user.setProfile(UserProfileEnum.getPerfilUsuarioOrThrow(input.profileCode()));
        }
    }
}
