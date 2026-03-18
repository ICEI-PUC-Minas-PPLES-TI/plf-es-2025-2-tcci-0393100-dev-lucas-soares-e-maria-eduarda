package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.model.User;
import br.pucminas.graphtest.application.exception.UpdatePasswordException;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCase;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserPasswordCommand;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserPasswordUseCaseImpl implements UpdateUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UpdateUserPasswordUseCaseImpl(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(UpdateUserPasswordCommand command) {

        User user = userRepository.findById(command.id())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuario nao encontrado com id: " + command.id()
                        )
                );

        if (command.senhaOriginal() != null && !command.senhaOriginal().isEmpty()) {
            if (!passwordEncoder.matches(command.senhaOriginal(), user.getPassword())) {
                throw new UpdatePasswordException("Senha atual incorreta");
            }
        }

        String encodedNewPassword = passwordEncoder.encode(command.senhaAtualizada());
        user.setPassword(encodedNewPassword);

        userRepository.save(user);
    }
}
