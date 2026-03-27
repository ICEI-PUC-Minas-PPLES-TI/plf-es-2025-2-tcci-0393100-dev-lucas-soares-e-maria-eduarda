package br.pucminas.graphtest.application.service.interfaces;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;

import java.util.UUID;

public interface UserAuthorizationService {
    AuthenticatedUser authorizeAdmin();

    AuthenticatedUser authorizeForUser(UUID userId);
}
