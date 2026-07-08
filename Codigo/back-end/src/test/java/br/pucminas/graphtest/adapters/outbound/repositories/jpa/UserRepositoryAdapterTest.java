package br.pucminas.graphtest.adapters.outbound.repositories.jpa;

import br.pucminas.graphtest.adapters.outbound.entities.jpa.user.JpaUserEntity;
import br.pucminas.graphtest.adapters.outbound.repositories.jpa.interfaces.JpaUserRepository;
import br.pucminas.graphtest.adapters.outbound.repositories.shared.BasePersistenceMapper;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private BasePersistenceMapper<User, JpaUserEntity> mapper;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @Test
    void shouldSaveUser() {
        User user = user();
        JpaUserEntity entity = new JpaUserEntity();
        when(mapper.toEntity(user)).thenReturn(entity);
        when(jpaUserRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(user);

        User saved = adapter.save(user);

        assertEquals(user, saved);
    }

    @Test
    void shouldFindUserById() {
        UUID userId = UUID.randomUUID();
        JpaUserEntity entity = new JpaUserEntity();
        User user = user();
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(user);

        Optional<User> result = adapter.findById(userId);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldFindAllUsers() {
        JpaUserEntity entity = new JpaUserEntity();
        User user = user();
        when(jpaUserRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(user);

        List<User> result = adapter.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void shouldDeleteUserById() {
        UUID userId = UUID.randomUUID();

        adapter.deleteById(userId);

        verify(jpaUserRepository).deleteById(userId);
    }

    @Test
    void shouldFindUserByEmail() {
        JpaUserEntity entity = new JpaUserEntity();
        User user = user();
        when(jpaUserRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(user);

        Optional<User> result = adapter.findByEmail("usuario@teste.com");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldCheckIfEmailExists() {
        when(jpaUserRepository.existsByEmail("usuario@teste.com")).thenReturn(true);
        when(jpaUserRepository.existsByEmail("ausente@teste.com")).thenReturn(false);

        assertTrue(adapter.existsByEmail("usuario@teste.com"));
        assertFalse(adapter.existsByEmail("ausente@teste.com"));
    }

    private User user() {
        return new User(UUID.randomUUID(), "Usuario", "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
    }
}
