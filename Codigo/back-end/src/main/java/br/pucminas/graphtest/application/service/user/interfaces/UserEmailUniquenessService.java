package br.pucminas.graphtest.application.service.user.interfaces;

import java.util.UUID;

/**
 * Centraliza a regra de unicidade do email de usuario na camada de aplicacao.
 */
public interface UserEmailUniquenessService {

    void ensureEmailAvailable(String email);

    void ensureEmailAvailableForUpdate(UUID userId, String email);
}
