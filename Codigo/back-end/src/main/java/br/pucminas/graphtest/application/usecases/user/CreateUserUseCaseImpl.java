package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.CreateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;

public class CreateUserUseCaseImpl implements CreateUserUseCasePort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;


    public CreateUserUseCaseImpl(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserOutput execute(CreateUserInput input) {

        existsByEmail(input.email());

        User user = new User(
                null,
                input.name(),
                input.email(),
                passwordEncoder.encode(input.password()),
                UserProfileEnum.USUARIO
        );

        return UserOutput.from(userRepository.save(user));
    }


    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Ja existe um usuario cadastrado com o email informado");
        }
    }


}
