package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserForUserUseCase;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCase;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;
    private final AuthorizeCurrentUserForUserUseCase authorizeCurrentUserForUserUseCase;

    public DeleteUserUseCaseImpl(UserRepository userRepository, AuthorizeCurrentUserForUserUseCase authorizeCurrentUserForUserUseCase) {
        this.userRepository = userRepository;
        this.authorizeCurrentUserForUserUseCase = authorizeCurrentUserForUserUseCase;
    }

    @Override
    public void execute(DeleteUserInput input) {
        authorizeCurrentUserForUserUseCase.execute(input.id());

        User user = userRepository.findById(input.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        userRepository.deleteById(user.getId());
    }
}
