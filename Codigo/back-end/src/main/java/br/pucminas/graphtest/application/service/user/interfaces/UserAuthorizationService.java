package br.pucminas.graphtest.application.service.user.interfaces;

import br.pucminas.graphtest.application.security.AuthenticatedUser;

import java.util.UUID;

public interface UserAuthorizationService {
    AuthenticatedUser authorizeAdmin();

    AuthenticatedUser authorizeForUser(UUID userId);
}
