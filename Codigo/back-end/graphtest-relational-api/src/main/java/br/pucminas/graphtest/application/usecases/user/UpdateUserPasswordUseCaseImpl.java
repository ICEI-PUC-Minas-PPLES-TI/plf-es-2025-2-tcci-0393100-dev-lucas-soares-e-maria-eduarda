package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.exception.EntidadeNaoEncontradaException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCase;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserPasswordCommand;
import br.pucminas.graphtest.application.port.output.PasswordEncoderPort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
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
                        new EntidadeNaoEncontradaException(
                                "Usuário não encontrado com id: " + command.id()
                        )
                );

        if (command.senhaOriginal() != null && !command.senhaOriginal().isEmpty()) {
            if (!passwordEncoder.matches(command.senhaOriginal(), user.getPassword())) {
                throw new IllegalArgumentException("Senha atual incorreta");
            }
        }

        String encodedNewPassword = passwordEncoder.encode(command.senhaAtualizada());
        user.setPassword(encodedNewPassword);

        userRepository.save(user);
    }
}
