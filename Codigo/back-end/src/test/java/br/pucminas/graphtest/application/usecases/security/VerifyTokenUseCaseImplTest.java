package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.port.input.security.records.TokenValidationResult;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyTokenUseCaseImplTest {

    @Mock
    private TokenServicePort tokenService;

    @InjectMocks
    private VerifyTokenUseCaseImpl useCase;

    @Test
    void shouldReturnValidResultWithEmailWhenTokenIsValid() {
        when(tokenService.tokenValido("token-valido")).thenReturn(true);
        when(tokenService.getEmailUsuario("token-valido")).thenReturn("usuario@teste.com");

        TokenValidationResult result = useCase.execute("token-valido");

        assertTrue(result.valid());
        assertEquals("usuario@teste.com", result.email());
    }

    @Test
    void shouldReturnInvalidResultWithoutEmailWhenTokenIsInvalid() {
        when(tokenService.tokenValido("token-invalido")).thenReturn(false);

        TokenValidationResult result = useCase.execute("token-invalido");

        assertFalse(result.valid());
        assertNull(result.email());
        verify(tokenService, never()).getEmailUsuario(anyString());
    }
}
