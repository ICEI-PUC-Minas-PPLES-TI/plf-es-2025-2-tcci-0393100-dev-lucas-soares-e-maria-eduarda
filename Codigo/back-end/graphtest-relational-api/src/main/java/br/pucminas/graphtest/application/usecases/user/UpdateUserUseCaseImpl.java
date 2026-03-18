package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.model.User;
import br.pucminas.graphtest.application.domain.model.UserProfileEnum;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserCommand;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResult execute(UpdateUserCommand command) {

        User usuarioCadastrado = userRepository.findById(command.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        usuarioCadastrado.setName(command.name());
        usuarioCadastrado.setEmail(command.email());

        if (UserProfileEnum.USUARIO.equals(usuarioCadastrado.getProfile())) {
            usuarioCadastrado.setProfile(UserProfileEnum.getPerfilUsuario(command.profileCode()));
        }

        return UserResult.from(userRepository.save(usuarioCadastrado));
    }
}
