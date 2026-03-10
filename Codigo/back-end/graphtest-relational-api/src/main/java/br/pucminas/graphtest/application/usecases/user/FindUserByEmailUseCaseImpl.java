package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.exception.EntidadeNaoEncontradaException;
import br.pucminas.graphtest.application.port.input.user.FindUserByEmailUseCase;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static java.lang.String.format;

@Service
public class FindUserByEmailUseCaseImpl implements FindUserByEmailUseCase {

    private final UserRepository userRepository;

    public FindUserByEmailUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (NoSuchElementException e) {
            throw new EntidadeNaoEncontradaException(
                    format("usuário não encontrado, email: %s", email)
            );
        }
    }
}
