package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.DeleteUserCommand;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DeleteUserCommand command) {
        User user = userRepository.findById(command.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
        userRepository.deleteById(user.getId());
    }
}
