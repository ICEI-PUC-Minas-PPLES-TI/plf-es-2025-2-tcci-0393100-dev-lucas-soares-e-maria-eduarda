package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.security.AuthenticatedUser;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.port.input.security.AuthenticatedUserByTokenUseCasePort;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;

public class AuthenticatedUserByTokenUseCaseImpl implements AuthenticatedUserByTokenUseCasePort {

    private final TokenServicePort tokenService;
    private final UserRepositoryPort userRepositoryPort;

    public AuthenticatedUserByTokenUseCaseImpl(TokenServicePort tokenService, UserRepositoryPort userRepositoryPort) {
        this.tokenService = tokenService;
        this.userRepositoryPort = userRepositoryPort;
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

        User user = userRepositoryPort.findByEmail(email).orElse(null);
        if (user == null) {
            return null;
        }

        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getProfile());
    }
}
