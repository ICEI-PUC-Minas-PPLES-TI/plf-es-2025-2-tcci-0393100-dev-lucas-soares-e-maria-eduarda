package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.exception.DeletarEntidadeException;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCase;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        try {
            userRepository.deleteById(user.getId());
        } catch (Exception e) {
            throw new DeletarEntidadeException(format("existem entidades relacionadas: %s", e));
        }
    }
}
