package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.exception.UpdatePasswordException;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserPasswordInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;

public class UpdateUserPasswordUseCaseImpl implements UpdateUserPasswordUseCasePort {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoder;
    private final UserAuthorizationService userAuthorizationService;

    public UpdateUserPasswordUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoder, UserAuthorizationService userAuthorizationService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.userAuthorizationService = userAuthorizationService;
    }

    @Override
    public void execute(UpdateUserPasswordInput input) {
        userAuthorizationService.authorizeForUser(input.id());

        User user = userRepositoryPort.findById(input.id())
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

        userRepositoryPort.save(user);
    }
}
