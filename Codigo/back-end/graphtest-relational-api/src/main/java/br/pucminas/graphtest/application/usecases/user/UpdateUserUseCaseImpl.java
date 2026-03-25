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
        AuthenticatedUser currentUser = userAuthorizationService.authorizeForUser(input.id());

        User usuarioCadastrado = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        userRepository.findByEmail(input.email())
                .filter(usuario -> !usuario.getId().equals(input.id()))
                .ifPresent(usuario -> {
                    throw new DuplicateEmailException("Ja existe um usuario cadastrado com o email informado");
                });

        usuarioCadastrado.setName(input.name());
        usuarioCadastrado.setEmail(input.email());

        if (currentUser.isAdmin() && input.profileCode() != null) {
            usuarioCadastrado.setProfile(UserProfileEnum.getPerfilUsuarioOrThrow(input.profileCode()));
        }

        return UserOutput.from(userRepository.save(usuarioCadastrado));
    }
}
