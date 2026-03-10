package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.CreateUserCommand;
import br.pucminas.graphtest.application.port.output.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;


    public CreateUserUseCaseImpl(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User execute(CreateUserCommand command) {

        User user = new User(
                null,
                command.name(),
                command.email(),
                passwordEncoder.encode(command.password()),
                UserProfileEnum.USUARIO.getCodigo()
        );

        return userRepository.save(user);
    }


}
