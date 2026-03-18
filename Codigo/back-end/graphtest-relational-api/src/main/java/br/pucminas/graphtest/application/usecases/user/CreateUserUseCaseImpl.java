package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.model.User;
import br.pucminas.graphtest.application.domain.model.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.CreateUserCommand;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
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
    public UserResult execute(CreateUserCommand command) {

        User user = new User(
                null,
                command.name(),
                command.email(),
                passwordEncoder.encode(command.password()),
                UserProfileEnum.USUARIO
        );

        return UserResult.from(userRepository.save(user));
    }
}
