package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.model.AuthenticatedUser;
import br.pucminas.graphtest.application.domain.model.User;
import br.pucminas.graphtest.application.domain.model.UserProfileEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserForUserUseCase;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserCommand;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final AuthorizeCurrentUserForUserUseCase authorizeCurrentUserForUserUseCase;

    public UpdateUserUseCaseImpl(UserRepository userRepository, AuthorizeCurrentUserForUserUseCase authorizeCurrentUserForUserUseCase) {
        this.authorizeCurrentUserForUserUseCase = authorizeCurrentUserForUserUseCase;
        this.userRepository = userRepository;

    }

    @Override
    public UserResult execute(UpdateUserCommand command) {
        AuthenticatedUser currentUser = authorizeCurrentUserForUserUseCase.execute(command.id());

        User usuarioCadastrado = userRepository.findById(command.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));


        usuarioCadastrado.setName(command.name());
        usuarioCadastrado.setEmail(command.email());

        if (currentUser.isAdmin()) {
            usuarioCadastrado.setProfile(UserProfileEnum.getPerfilUsuario(command.profileCode()));
        }

        return UserResult.from(userRepository.save(usuarioCadastrado));
    }
}
