package br.pucminas.graphtest.adapters.inbound.controller;


import br.pucminas.graphtest.adapters.inbound.controller.interfaces.UserController;
import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.dto.UserDTO;
import br.pucminas.graphtest.domain.User;
import br.pucminas.graphtest.application.usecases.UserUseCase;
import br.pucminas.graphtest.util.ConversorEntidadeDTOUtil;
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
import static br.pucminas.graphtest.util.ConstantesRequisicaoUtil.*;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.USUARIO_CONTROLLER;
import static br.pucminas.graphtest.util.ContrutorRespostaJsonUtil.construirRespostaJSON;
import static br.pucminas.graphtest.util.ConversorEntidadeDTOUtil.converterParaDTO;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = USUARIO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_USUARIO)
@AllArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserUseCase userService; //polimorfismo


    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody User usuario) {
        log.info(">>> criar: recebendo requisição para criar usuário");
        User usuarioCriado = userService.criar(usuario);

        return ResponseEntity.created (URI.create("/usuario/" + usuarioCriado.getId())).body (construirRespostaJSON(CHAVES_USUARIO_CONTROLLER, asList(CREATED.value(), MSG_USUARIO_CRIADO, usuarioCriado.getId())));

    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> encontrarPorId(@PathVariable UUID id) {
        log.info(">>> encontrarPorId: recebendo requisição para encontrar usuário por id");
        User usuario = userService.encontrarPorId(id);
        return ResponseEntity.ok().body(converterParaDTO(usuario));
    }

    @Override
    public ResponseEntity<List<UserDTO>> listarTodos() {
        log.info(">>> listarTodos: recebendo requisição para listar todos usuários");
        List<User> usuarios = userService.listarTodos();
        return ResponseEntity.ok().body(usuarios.stream().map(ConversorEntidadeDTOUtil::converterParaDTO).toList());
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable UUID id,@Valid @RequestBody @NotNull User usuario) {
        log.info(">>> atualizar: recebendo requisição para atualizar usuário");
        usuario.setId(id);
        User usuarioAtualizado = userService.atualizar(usuario);
        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_USUARIO_CONTROLLER, asList(OK.value(), MSG_USUARIO_ATUALIZADO, usuarioAtualizado.getId())));
    }

    @Override
    public ResponseEntity<Map<String, Object>> atualizarSenha(UUID id, PasswordDTO passwordDTO) {
        return null;
    }

    @Override
    public ResponseEntity<?> verificarToken(String token) {
        return null;
    }


    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(UUID id) {
        log.info(">>> deletar: recebendo requisição para deletar usuário");
        userService.deletar(id);
        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_USUARIO_CONTROLLER, asList(OK.value(), MSG_USUARIO_DELETADO, id)));
    }
}
