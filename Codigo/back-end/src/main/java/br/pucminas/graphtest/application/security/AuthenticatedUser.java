package br.pucminas.graphtest.application.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
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
