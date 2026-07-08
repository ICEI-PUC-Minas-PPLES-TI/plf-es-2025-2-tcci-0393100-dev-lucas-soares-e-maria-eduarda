package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.dto.security.TokenValidationDTO;
import br.pucminas.graphtest.adapters.inbound.dto.user.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.user.UserDTO;
import br.pucminas.graphtest.application.port.input.security.VerifyTokenUseCasePort;
import br.pucminas.graphtest.application.port.input.security.records.TokenValidationResult;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCasePort;
import br.pucminas.graphtest.application.port.input.user.ListUsersUseCasePort;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCasePort;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCasePort;
import br.pucminas.graphtest.application.port.input.user.records.CreateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByIdInput;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserPasswordInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @Mock
    private CreateUserUseCasePort criarUsuarioUseCase;
    @Mock
    private DeleteUserUseCasePort deletarUsuarioUseCase;
    @Mock
    private FindUserByIdUseCasePort encontrarUsuarioPorIdUseCase;
    @Mock
    private ListUsersUseCasePort listarTodosUsuariosUseCase;
    @Mock
    private UpdateUserUseCasePort atualizarUsuarioUseCase;
    @Mock
    private UpdateUserPasswordUseCasePort atualizarSenhaUsuarioUseCase;
    @Mock
    private VerifyTokenUseCasePort verificarTokenUseCase;

    @InjectMocks
    private UserControllerImpl controller;

    private UserOutput userOutput(UUID id) {
        return new UserOutput(id, "Usuario Teste", "usuario@teste.com", 2, null, null);
    }

    @Test
    void shouldCreateUserAndReturnLocationHeader() {
        UUID userId = UUID.randomUUID();
        when(criarUsuarioUseCase.execute(new CreateUserInput("Usuario Teste", "usuario@teste.com", "senha123")))
                .thenReturn(userOutput(userId));

        UserDTO requestBody = UserDTO.builder().name("Usuario Teste").email("usuario@teste.com").password("senha123").build();
        ResponseEntity<Map<String, Object>> response = controller.create(requestBody);

        assertEquals(CREATED, response.getStatusCode());
        assertTrue(response.getHeaders().getLocation().toString().endsWith("/usuario/" + userId));
    }

    @Test
    void shouldFindUserById() {
        UUID userId = UUID.randomUUID();
        when(encontrarUsuarioPorIdUseCase.execute(new FindUserByIdInput(userId))).thenReturn(userOutput(userId));

        ResponseEntity<UserDTO> response = controller.findById(userId);

        assertEquals(OK, response.getStatusCode());
        assertEquals(userId, response.getBody().id());
        assertEquals("usuario@teste.com", response.getBody().email());
    }

    @Test
    void shouldListAllUsers() {
        UUID userId = UUID.randomUUID();
        when(listarTodosUsuariosUseCase.execute()).thenReturn(List.of(userOutput(userId)));

        ResponseEntity<List<UserDTO>> response = controller.listAll();

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldUpdateUser() {
        UUID userId = UUID.randomUUID();
        when(atualizarUsuarioUseCase.execute(new UpdateUserInput(userId, "Novo Nome", "novo@teste.com", 1)))
                .thenReturn(userOutput(userId));

        UserDTO requestBody = UserDTO.builder().name("Novo Nome").email("novo@teste.com").profileUser(1).build();
        ResponseEntity<Map<String, Object>> response = controller.update(userId, requestBody);

        assertEquals(OK, response.getStatusCode());
        verify(atualizarUsuarioUseCase).execute(new UpdateUserInput(userId, "Novo Nome", "novo@teste.com", 1));
    }

    @Test
    void shouldUpdateUserPassword() {
        UUID userId = UUID.randomUUID();
        PasswordDTO passwordDTO = PasswordDTO.builder().senhaOriginal("senha-antiga").senhaAtualizada("senha-nova").build();

        ResponseEntity<Map<String, Object>> response = controller.updatePassword(userId, passwordDTO);

        assertEquals(OK, response.getStatusCode());
        verify(atualizarSenhaUsuarioUseCase).execute(new UpdateUserPasswordInput(userId, "senha-antiga", "senha-nova"));
    }

    @Test
    void shouldVerifyToken() {
        when(verificarTokenUseCase.execute("token-valido")).thenReturn(new TokenValidationResult(true, "usuario@teste.com"));

        ResponseEntity<TokenValidationDTO> response = controller.verifyToken("token-valido");

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().valid());
        assertEquals("usuario@teste.com", response.getBody().email());
    }

    @Test
    void shouldDeleteUser() {
        UUID userId = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response = controller.delete(userId);

        assertEquals(OK, response.getStatusCode());
        verify(deletarUsuarioUseCase).execute(new DeleteUserInput(userId));
    }
}
