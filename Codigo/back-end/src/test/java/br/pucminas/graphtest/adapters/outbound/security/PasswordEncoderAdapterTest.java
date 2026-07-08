package br.pucminas.graphtest.adapters.outbound.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderAdapter adapter;

    @Test
    void shouldDelegateEncodingToUnderlyingEncoder() {
        when(passwordEncoder.encode("senha123")).thenReturn("hash-codificado");

        assertEquals("hash-codificado", adapter.encode("senha123"));
    }

    @Test
    void shouldDelegateMatchesToUnderlyingEncoder() {
        when(passwordEncoder.matches("senha123", "hash-codificado")).thenReturn(true);

        assertTrue(adapter.matches("senha123", "hash-codificado"));
    }
}
