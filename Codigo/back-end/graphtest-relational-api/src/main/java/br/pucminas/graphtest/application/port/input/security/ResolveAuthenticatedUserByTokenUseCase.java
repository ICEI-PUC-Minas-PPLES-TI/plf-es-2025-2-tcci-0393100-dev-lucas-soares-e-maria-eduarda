package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.domain.model.AuthenticatedUser;
import jakarta.annotation.Nullable;

public interface ResolveAuthenticatedUserByTokenUseCase {

    @Nullable AuthenticatedUser execute(String token);
}
