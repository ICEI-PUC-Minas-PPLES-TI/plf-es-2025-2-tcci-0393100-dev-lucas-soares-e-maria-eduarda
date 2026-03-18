package br.pucminas.graphtest.application.domain.model;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String username,
        UserProfileEnum profile
) {
    public boolean isAdmin() {
        return UserProfileEnum.ADMIN.equals(profile);
    }
}
