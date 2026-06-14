package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.CreateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserEmailUniquenessService;

import java.time.LocalDateTime;

public class CreateUserUseCaseImpl implements CreateUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserEmailUniquenessService userEmailUniquenessService;


    public CreateUserUseCaseImpl(UserRepositoryPort userRepository,
                                 PasswordEncoderPort passwordEncoder,
                                 UserEmailUniquenessService userEmailUniquenessService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEmailUniquenessService = userEmailUniquenessService;
    }

    @Override
    public UserOutput execute(CreateUserInput input) {

        userEmailUniquenessService.ensureEmailAvailable(input.email());

        User user = new User(
                null,
                input.name(),
                input.email(),
                passwordEncoder.encode(input.password()),
                UserProfileEnum.USUARIO,
                LocalDateTime.now(),
                null
        );

        return UserOutput.from(userRepository.save(user));
    }
}
