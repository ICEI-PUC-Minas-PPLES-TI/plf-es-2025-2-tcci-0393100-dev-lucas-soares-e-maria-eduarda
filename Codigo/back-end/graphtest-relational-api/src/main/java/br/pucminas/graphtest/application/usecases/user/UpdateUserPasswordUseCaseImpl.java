package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.User;
import br.pucminas.graphtest.application.exception.UpdatePasswordException;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCase;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserPasswordInput;
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
    public void execute(UpdateUserPasswordInput input) {

        User user = userRepository.findById(input.id())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuario nao encontrado com id: " + input.id()
                        )
                );

        if (input.senhaOriginal() != null && !input.senhaOriginal().isEmpty()) {
            if (!passwordEncoder.matches(input.senhaOriginal(), user.getPassword())) {
                throw new UpdatePasswordException("Senha atual incorreta");
            }
        }

        String encodedNewPassword = passwordEncoder.encode(input.senhaAtualizada());
        user.setPassword(encodedNewPassword);

        userRepository.save(user);
    }
}
