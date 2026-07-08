package br.pucminas.graphtest.adapters.outbound.repositories.jpa.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.user.JpaUserEntity;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperBaseTest {

    private final UserMapperBase mapper = new UserMapperBase();

    @Test
    void shouldReturnNullWhenConvertingNullUserToEntity() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldReturnNullWhenConvertingNullEntityToDomain() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void shouldConvertUserToEntity() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash", UserProfileEnum.ADMIN, now, now);

        JpaUserEntity entity = mapper.toEntity(user);

        assertEquals(userId, entity.getId());
        assertEquals("Usuario", entity.getName());
        assertEquals("usuario@teste.com", entity.getEmail());
        assertEquals("hash", entity.getPassword());
        assertEquals(UserProfileEnum.ADMIN.getCodigo(), entity.getPerfilUsuario());
    }

    @Test
    void shouldConvertEntityToUser() {
        UUID userId = UUID.randomUUID();
        JpaUserEntity entity = new JpaUserEntity();
        entity.setId(userId);
        entity.setName("Usuario");
        entity.setEmail("usuario@teste.com");
        entity.setPassword("hash");
        entity.setPerfilUsuario(UserProfileEnum.USUARIO.getCodigo());

        User user = mapper.toDomain(entity);

        assertEquals(userId, user.getId());
        assertEquals("usuario@teste.com", user.getEmail());
        assertEquals(UserProfileEnum.USUARIO, user.getProfile());
    }

    @Test
    void shouldMapNullProfileCodeToNullProfile() {
        JpaUserEntity entity = new JpaUserEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Usuario");
        entity.setEmail("usuario@teste.com");
        entity.setPassword("hash");
        entity.setPerfilUsuario(null);

        User user = mapper.toDomain(entity);

        assertNull(user.getProfile());
    }
}
