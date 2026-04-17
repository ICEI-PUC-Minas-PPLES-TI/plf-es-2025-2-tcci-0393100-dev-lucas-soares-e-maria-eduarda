package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.UserController;
import br.pucminas.graphtest.adapters.inbound.controller.interfaces.OperacoesCRUDController;
import br.pucminas.graphtest.adapters.inbound.dto.user.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.security.TokenValidationDTO;
import br.pucminas.graphtest.adapters.inbound.dto.user.UserDTO;
import br.pucminas.graphtest.adapters.inbound.util.EntityDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.security.VerifyTokenUseCasePort;
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
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.CHAVES_USUARIO_CONTROLLER;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_ATUALIZADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_CRIADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_DELETADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_SENHA;
import static br.pucminas.graphtest.adapters.inbound.util.EntityDtoConverterUtil.toDto;
import static br.pucminas.graphtest.adapters.inbound.util.JsonResponseBuilderUtil.buildJsonResponse;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO_SENHA;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO_VERIFICAR_TOKEN;
import static br.pucminas.graphtest.shared.LogTopicsUtil.USUARIO_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = USUARIO_CONTROLLER)
@RestController
@Validated
@RequestMapping(USUARIO)
@AllArgsConstructor
public class UserControllerImpl implements UserController, OperacoesCRUDController<UserDTO, UserDTO> {

    private final CreateUserUseCasePort criarUsuarioUseCase;
    private final DeleteUserUseCasePort deletarUsuarioUseCase;
    private final FindUserByIdUseCasePort encontrarUsuarioPorIdUseCase;
    private final ListUsersUseCasePort listarTodosUsuariosUseCase;
    private final UpdateUserUseCasePort atualizarUsuarioUseCase;
    private final UpdateUserPasswordUseCasePort atualizarSenhaUsuarioUseCase;
    private final VerifyTokenUseCasePort verificarTokenUseCase;

    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Validated(UserDTO.Create.class) @RequestBody UserDTO usuario
    ) {
        log.info(">>> criar: recebendo requisicao para criar usuario");

        CreateUserInput input = new CreateUserInput(
                usuario.name(),
                usuario.email(),
                usuario.password()
        );

        UserOutput usuarioCriado = criarUsuarioUseCase.execute(input);

        return ResponseEntity.created(URI.create(USUARIO + "/" + usuarioCriado.id()))
                .body(buildJsonResponse(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(CREATED.value(), MSG_USUARIO_CRIADO, usuarioCriado.id())
                ));
    }

    @Override
    @GetMapping(ID)
    public ResponseEntity<UserDTO> findById(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisicao para encontrar usuario por id");

        UserOutput usuario = encontrarUsuarioPorIdUseCase.execute(new FindUserByIdInput(id));

        return ResponseEntity.ok().body(toDto(usuario));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAll() {
        log.info(">>> listarTodos: recebendo requisicao para listar todos usuarios");

        List<UserOutput> usuarios = listarTodosUsuariosUseCase.execute();

        return ResponseEntity.ok()
                .body(usuarios.stream()
                        .map(EntityDtoConverterUtil::toDto)
                        .toList());
    }

    @Override
    @PutMapping(ID)
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable UUID id,
            @Validated(UserDTO.Update.class) @RequestBody UserDTO usuario
    ) {
        log.info(">>> atualizar: recebendo requisicao para atualizar usuario");

        UpdateUserInput input = new UpdateUserInput(
                id,
                usuario.name(),
                usuario.email(),
                usuario.profileUser()
        );

        UserOutput usuarioAtualizado = atualizarUsuarioUseCase.execute(input);

        return ResponseEntity.ok()
                .body(buildJsonResponse(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_ATUALIZADO, usuarioAtualizado.id())
                ));
    }

    @Override
    @PatchMapping(USUARIO_SENHA)
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable UUID id,
            @Valid @RequestBody PasswordDTO passwordDTO
    ) {
        log.info(">>> atualizarSenha: recebendo requisicao para atualizar senha do usuario id: {}", id);

        UpdateUserPasswordInput input = new UpdateUserPasswordInput(
                id,
                passwordDTO.senhaOriginal(),
                passwordDTO.senhaAtualizada()
        );

        atualizarSenhaUsuarioUseCase.execute(input);

        return ResponseEntity.ok()
                .body(buildJsonResponse(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_SENHA, id)
                ));
    }

    @Override
    @GetMapping(USUARIO_VERIFICAR_TOKEN)
    public ResponseEntity<TokenValidationDTO> verifyToken(@RequestParam("token") String token) {
        log.info(">>> verificarToken: recebendo requisicao para verificar token");
        return ResponseEntity.ok(TokenValidationDTO.from(verificarTokenUseCase.execute(token)));
    }

    @Override
    @DeleteMapping(ID)
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisicao para deletar usuario");

        deletarUsuarioUseCase.execute(new DeleteUserInput(id));

        return ResponseEntity.ok()
                .body(buildJsonResponse(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_DELETADO, id)
                ));
    }
}
