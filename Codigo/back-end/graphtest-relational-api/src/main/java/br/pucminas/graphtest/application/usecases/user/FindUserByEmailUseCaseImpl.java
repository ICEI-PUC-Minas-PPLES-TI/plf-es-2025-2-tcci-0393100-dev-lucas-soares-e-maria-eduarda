package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.FindUserByEmailUseCase;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByEmailInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class FindUserByEmailUseCaseImpl implements FindUserByEmailUseCase {

    private final UserRepository userRepository;

    public FindUserByEmailUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserOutput execute(FindUserByEmailInput input) {
        return userRepository.findByEmail(input.email())
                .map(UserOutput::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("usuario nao encontrado, email: %s", input.email())
                ));
    }
}
