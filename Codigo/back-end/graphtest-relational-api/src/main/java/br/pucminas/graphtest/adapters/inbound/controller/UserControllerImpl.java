package br.pucminas.graphtest.adapters.inbound.controller;

import br.pucminas.graphtest.adapters.inbound.controller.interfaces.UserController;
import br.pucminas.graphtest.adapters.inbound.dto.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.TokenValidationDTO;
import br.pucminas.graphtest.adapters.inbound.dto.UserDTO;
import br.pucminas.graphtest.adapters.inbound.util.EntityDtoConverterUtil;
import br.pucminas.graphtest.application.port.input.security.VerifyTokenUseCase;
import br.pucminas.graphtest.application.port.input.user.CreateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.DeleteUserUseCase;
import br.pucminas.graphtest.application.port.input.user.FindUserByIdUseCase;
import br.pucminas.graphtest.application.port.input.user.ListUsersUseCase;
import br.pucminas.graphtest.application.port.input.user.UpdateUserPasswordUseCase;
import br.pucminas.graphtest.application.port.input.user.UpdateUserUseCase;
import br.pucminas.graphtest.application.port.input.user.command.CreateUserCommand;
import br.pucminas.graphtest.application.port.input.user.command.DeleteUserCommand;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserCommand;
import br.pucminas.graphtest.application.port.input.user.command.UpdateUserPasswordCommand;
import br.pucminas.graphtest.application.port.input.user.query.FindUserByIdQuery;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.ENDPOINT_USUARIO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_ATUALIZADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_CRIADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_DELETADO;
import static br.pucminas.graphtest.adapters.inbound.util.ControllerConstantsUtil.MSG_USUARIO_SENHA;
import static br.pucminas.graphtest.adapters.inbound.util.EntityDtoConverterUtil.converterParaDTO;
import static br.pucminas.graphtest.adapters.inbound.util.JsonResponseBuilderUtil.construirRespostaJSON;
import static br.pucminas.graphtest.shared.logging.LogTopics.USUARIO_CONTROLLER;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = USUARIO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_USUARIO)
@AllArgsConstructor
/**
 * Implementacao do controlador REST responsavel pelas operacoes de usuario.
 *
 * <p>Esta classe recebe as requisicoes HTTP relacionadas ao recurso de usuario,
 * aplica as validacoes de entrada e delega o processamento para os casos de uso
 * da camada de aplicacao.</p>
 */
public class UserControllerImpl implements UserController {

    private final CreateUserUseCase criarUsuarioUseCase;
    private final DeleteUserUseCase deletarUsuarioUseCase;
    private final FindUserByIdUseCase encontrarUsuarioPorIdUseCase;
    private final ListUsersUseCase listarTodosUsuariosUseCase;
    private final UpdateUserUseCase atualizarUsuarioUseCase;
    private final UpdateUserPasswordUseCase atualizarSenhaUsuarioUseCase;
    private final VerifyTokenUseCase verificarTokenUseCase;

    /**
     * Cria um novo usuario a partir dos dados informados na requisicao.
     *
     * @param usuario dados do usuario a ser criado
     * @return resposta HTTP 201 contendo uma mensagem de sucesso e o identificador do usuario criado
     */
    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(
            @Validated(UserDTO.Create.class) @RequestBody UserDTO usuario
    ) {
        log.info(">>> criar: recebendo requisicao para criar usuario");

        CreateUserCommand command = new CreateUserCommand(
                usuario.name(),
                usuario.email(),
                usuario.password()
        );

        UserResult usuarioCriado = criarUsuarioUseCase.execute(command);

        return ResponseEntity.created(URI.create("/usuario/" + usuarioCriado.id()))
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(CREATED.value(), MSG_USUARIO_CRIADO, usuarioCriado.id())
                ));
    }

    /**
     * Busca um usuario pelo seu identificador unico.
     *
     * @param id identificador do usuario
     * @return resposta HTTP 200 contendo os dados do usuario encontrado
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> encontrarPorId(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisicao para encontrar usuario por id");

        UserResult usuario = encontrarUsuarioPorIdUseCase.execute(new FindUserByIdQuery(id));

        return ResponseEntity.ok().body(converterParaDTO(usuario));
    }

    /**
     * Lista todos os usuarios cadastrados.
     *
     * @return resposta HTTP 200 contendo a lista de usuarios
     */
    @Override
    @GetMapping
    public ResponseEntity<List<UserDTO>> listarTodos() {
        log.info(">>> listarTodos: recebendo requisicao para listar todos usuarios");

        List<UserResult> usuarios = listarTodosUsuariosUseCase.execute();

        return ResponseEntity.ok()
                .body(usuarios.stream()
                        .map(EntityDtoConverterUtil::converterParaDTO)
                        .toList());
    }

    /**
     * Atualiza os dados cadastrais de um usuario existente.
     *
     * @param id identificador do usuario a ser atualizado
     * @param usuario novos dados do usuario
     * @return resposta HTTP 200 contendo uma mensagem de sucesso e o identificador do usuario atualizado
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(
            @PathVariable UUID id,
            @Validated(UserDTO.Update.class) @RequestBody @NotNull UserDTO usuario
    ) {
        log.info(">>> atualizar: recebendo requisicao para atualizar usuario");

        UpdateUserCommand command = new UpdateUserCommand(
                id,
                usuario.name(),
                usuario.email(),
                usuario.profileUser()
        );

        UserResult usuarioAtualizado = atualizarUsuarioUseCase.execute(command);

        return ResponseEntity.ok()
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_ATUALIZADO, usuarioAtualizado.id())
                ));
    }

    /**
     * Atualiza a senha de um usuario existente.
     *
     * @param id identificador do usuario
     * @param passwordDTO dados contendo a senha atual e a nova senha
     * @return resposta HTTP 200 contendo uma mensagem de sucesso e o identificador do usuario
     */
    @Override
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Map<String, Object>> atualizarSenha(
            @PathVariable UUID id,
            @Valid @RequestBody PasswordDTO passwordDTO
    ) {
        log.info(">>> atualizarSenha: recebendo requisicao para atualizar senha do usuario id: {}", id);

        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand(
                id,
                passwordDTO.senhaOriginal(),
                passwordDTO.senhaAtualizada()
        );

        atualizarSenhaUsuarioUseCase.execute(command);

        return ResponseEntity.ok()
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_SENHA, id)
                ));
    }

    /**
     * Verifica a validade de um token informado pelo cliente.
     *
     * @param token token a ser validado
     * @return resposta HTTP 200 contendo o resultado da validacao do token
     */
    @Override
    @GetMapping("/verificar-token")
    public ResponseEntity<TokenValidationDTO> verificarToken(@RequestParam("token") String token) {
        log.info(">>> verificarToken: recebendo requisicao para verificar token");
        return ResponseEntity.ok(TokenValidationDTO.from(verificarTokenUseCase.execute(token)));
    }

    /**
     * Remove um usuario com base no identificador informado.
     *
     * @param id identificador do usuario a ser removido
     * @return resposta HTTP 200 contendo uma mensagem de sucesso e o identificador do usuario removido
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisicao para deletar usuario");

        DeleteUserCommand command = new DeleteUserCommand(id);

        deletarUsuarioUseCase.execute(command);

        return ResponseEntity.ok()
                .body(construirRespostaJSON(
                        CHAVES_USUARIO_CONTROLLER,
                        asList(OK.value(), MSG_USUARIO_DELETADO, id)
                ));
    }
}
