package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.AuthenticatedUser;
import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.domain.UserProfileEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserForUserUseCase;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
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
    public UserOutput execute(UpdateUserInput input) {
        AuthenticatedUser currentUser = authorizeCurrentUserForUserUseCase.execute(input.id());

        User usuarioCadastrado = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));


        usuarioCadastrado.setName(input.name());
        usuarioCadastrado.setEmail(input.email());

        if (currentUser.isAdmin()) {
            usuarioCadastrado.setProfile(UserProfileEnum.getPerfilUsuario(input.profileCode()));
        }

        return UserOutput.from(userRepository.save(usuarioCadastrado));
    }
}
