package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.AuthenticatedUser;
import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.port.input.security.ResolveAuthenticatedUserByTokenUseCase;
import br.pucminas.graphtest.application.port.output.repositories.UserRepository;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import org.springframework.stereotype.Service;

@Service
public class ResolveAuthenticatedUserByTokenUseCaseImpl implements ResolveAuthenticatedUserByTokenUseCase {

    private final TokenServicePort tokenService;
    private final UserRepository userRepository;

    public ResolveAuthenticatedUserByTokenUseCaseImpl(TokenServicePort tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticatedUser execute(String token) {
        if (!tokenService.tokenValido(token)) {
            return null;
        }

        String email = tokenService.getEmailUsuario(token);
        if (email == null) {
            return null;
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return null;
        }

        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getProfile());
    }
}
