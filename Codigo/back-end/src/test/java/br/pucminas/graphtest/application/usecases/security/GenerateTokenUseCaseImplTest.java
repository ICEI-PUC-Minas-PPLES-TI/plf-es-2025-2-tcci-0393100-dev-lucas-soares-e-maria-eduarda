package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.security.records.GenerateTokenInput;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateTokenUseCaseImplTest {

    @Mock
    private TokenServicePort tokenService;

    @InjectMocks
    private GenerateTokenUseCaseImpl useCase;

    @Test
    void shouldGenerateTokenUsingLowercasedProfileName() {
        UUID userId = UUID.randomUUID();
        when(tokenService.gerarToken("usuario@teste.com", "admin", userId)).thenReturn("token-gerado");

        String token = useCase.execute(new GenerateTokenInput(userId, "usuario@teste.com", UserProfileEnum.ADMIN));

        assertEquals("token-gerado", token);
        verify(tokenService).gerarToken("usuario@teste.com", "admin", userId);
    }
}
