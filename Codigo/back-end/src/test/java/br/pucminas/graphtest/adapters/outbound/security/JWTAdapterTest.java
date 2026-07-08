package br.pucminas.graphtest.adapters.outbound.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTAdapterTest {

    private final JWTAdapter adapter = new JWTAdapter();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "jwtSegredo", "0123456789abcdef0123456789abcdef0123456789abcdef");
        ReflectionTestUtils.setField(adapter, "tempoExpiracao", 60_000L);
    }

    @Test
    void shouldGenerateValidTokenAndExposeEmail() {
        UUID userId = UUID.randomUUID();
        String token = adapter.gerarToken("usuario@teste.com", "ADMIN", userId);

        assertTrue(adapter.tokenValido(token));
        assertEquals("usuario@teste.com", adapter.getEmailUsuario(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        ReflectionTestUtils.setField(adapter, "tempoExpiracao", -60_000L);
        String token = adapter.gerarToken("usuario@teste.com", "ADMIN", UUID.randomUUID());

        assertFalse(adapter.tokenValido(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        assertFalse(adapter.tokenValido("token-invalido"));
        assertNull(adapter.getEmailUsuario("token-invalido"));
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {
        String token = adapter.gerarToken("usuario@teste.com", "ADMIN", UUID.randomUUID());

        JWTAdapter otherAdapter = new JWTAdapter();
        ReflectionTestUtils.setField(otherAdapter, "jwtSegredo", "fedcba9876543210fedcba9876543210fedcba9876543210");
        ReflectionTestUtils.setField(otherAdapter, "tempoExpiracao", 60_000L);

        assertFalse(otherAdapter.tokenValido(token));
    }
}
