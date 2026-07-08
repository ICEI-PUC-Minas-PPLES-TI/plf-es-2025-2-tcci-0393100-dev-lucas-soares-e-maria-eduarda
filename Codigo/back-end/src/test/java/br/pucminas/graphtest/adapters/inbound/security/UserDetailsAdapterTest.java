package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsAdapterTest {

    @Test
    void shouldExposeUserDetailsContractFromBuilderFields() {
        UUID id = UUID.randomUUID();
        UserDetailsAdapter adapter = UserDetailsAdapter.builder()
                .id(id)
                .email("usuario@teste.com")
                .senha("hash-senha")
                .perfilUsuario(UserProfileEnum.ADMIN)
                .build();

        assertEquals(id, adapter.getId());
        assertEquals("usuario@teste.com", adapter.getUsername());
        assertEquals("hash-senha", adapter.getPassword());
        assertEquals(1, adapter.getAuthorities().size());
        assertEquals("ROLE_ADMIN", adapter.getAuthorities().iterator().next().getAuthority());
        assertTrue(adapter.isAccountNonExpired());
        assertTrue(adapter.isAccountNonLocked());
        assertTrue(adapter.isCredentialsNonExpired());
        assertTrue(adapter.isEnabled());
    }
}
