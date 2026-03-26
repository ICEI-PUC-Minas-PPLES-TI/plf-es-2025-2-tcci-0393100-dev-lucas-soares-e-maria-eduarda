package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;

public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final UserAuthorizationService userAuthorizationService;

    public UpdateUserUseCaseImpl(UserRepository userRepository, UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
        this.userRepository = userRepository;

    }

    @Override
    public UserOutput execute(UpdateUserInput input) {
        AuthenticatedUser currentUser = authorizeUser(input);
        User user = findRegisteredUser(input);

        validateEmailUniqueness(input);
        updateUserData(input, currentUser, user);

        return UserOutput.from(userRepository.save(user));
    }

    private AuthenticatedUser authorizeUser(UpdateUserInput input) {
        return userAuthorizationService.authorizeForUser(input.id());
    }

    private User findRegisteredUser(UpdateUserInput input) {
        return userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
    }

    private void validateEmailUniqueness(UpdateUserInput input) {
        userRepository.findByEmail(input.email())
                .filter(user -> !user.getId().equals(input.id()))
                .ifPresent(user -> {
                    throw new DuplicateEmailException("Ja existe um usuario cadastrado com o email informado");
                });
    }

    private void updateUserData(UpdateUserInput input, AuthenticatedUser currentUser, User user) {
        user.setName(input.name());
        user.setEmail(input.email());
        updateProfileIfAllowed(input, currentUser, user);
    }

    private void updateProfileIfAllowed(UpdateUserInput input, AuthenticatedUser currentUser, User user) {
        if (currentUser.isAdmin() && input.profileCode() != null) {
            user.setProfile(UserProfileEnum.getPerfilUsuarioOrThrow(input.profileCode()));
        }
    }
}
