package br.pucminas.graphtest.adapters.inbound.controller;


import br.pucminas.graphtest.adapters.inbound.controller.interfaces.UserController;
import br.pucminas.graphtest.adapters.inbound.dto.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.UserDTO;
import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.port.input.user.*;
import br.pucminas.graphtest.adapters.inbound.util.ConversorEntidadeDTOUtil;
import br.pucminas.graphtest.application.port.input.user.command.CreateUserCommand;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserCommand;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserPasswordCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static br.pucminas.graphtest.adapters.inbound.util.ConstantesRequisicaoUtil.*;
import static br.pucminas.graphtest.infrastructure.util.ConstantesTopicosUtil.USUARIO_CONTROLLER;
import static br.pucminas.graphtest.adapters.inbound.util.ContrutorRespostaJsonUtil.*;
import static br.pucminas.graphtest.adapters.inbound.util.ConversorEntidadeDTOUtil.converterParaDTO;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = USUARIO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_USUARIO)
@AllArgsConstructor
public class UserControllerImpl implements UserController {

    private final CreateUserUseCase criarUsuarioUseCase;
    private final DeleteUserUseCase deletarUsuarioUseCase;
    private final FindUserByIdUseCase encontrarUsuarioPorIdUseCase;
    private final ListUsersUseCase listarTodosUsuariosUseCase;
    private final UpdateUserUseCase atualizarUsuarioUseCase;
    private final UpdateUserPasswordUseCase atualizarSenhaUsuarioUseCase;

    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody User usuario) {
        log.info(">>> criar: recebendo requisição para criar usuário");

        CreateUserCommand command = new CreateUserCommand(
                usuario.getName(),
                usuario.getEmail(),
                usuario.getPassword()
        );

        User usuarioCriado = criarUsuarioUseCase.execute(command);

        return ResponseEntity.created(URI.create("/usuario/" + usuarioCriado.getId()))
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(CREATED.value(), MSG_USUARIO_CRIADO, usuarioCriado.getId())
                ));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> encontrarPorId(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisição para encontrar usuário por id");

        User usuario = encontrarUsuarioPorIdUseCase.execute(id);

        return ResponseEntity.ok().body(converterParaDTO(usuario));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserDTO>> listarTodos() {
        log.info(">>> listarTodos: recebendo requisição para listar todos usuários");

        List<User> usuarios = listarTodosUsuariosUseCase.execute();

        return ResponseEntity.ok()
                .body(usuarios.stream()
                        .map(ConversorEntidadeDTOUtil::converterParaDTO)
                        .toList());
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody @NotNull User usuario
    ) {
        log.info(">>> atualizar: recebendo requisição para atualizar usuário");

        UpdateUserCommand command = new UpdateUserCommand(
                id,
                usuario.getName(),
                usuario.getEmail(),
                usuario.getPerfilUsuario()
        );

        User usuarioAtualizado = atualizarUsuarioUseCase.execute(command);

        return ResponseEntity.ok()
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_ATUALIZADO, usuarioAtualizado.getId())
                ));
    }

    @Override
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Map<String, Object>> atualizarSenha(
            @PathVariable UUID id,
            @Valid @RequestBody PasswordDTO passwordDTO
    ) {
        log.info(">>> atualizarSenha: recebendo requisição para atualizar senha do usuário id: {}", id);

        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand(
                id,
                passwordDTO.senhaOriginal(),
                passwordDTO.senhaAtualizada()
        );

        atualizarSenhaUsuarioUseCase.execute(command);

        return ResponseEntity.ok()
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), "Senha atualizada com sucesso", id)
                ));
    }

    @Override
    public ResponseEntity<?> verificarToken(String token) {
        return null;
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisição para deletar usuário");

        deletarUsuarioUseCase.execute(id);

        return ResponseEntity.ok()
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_DELETADO, id)
                ));
    }
}
