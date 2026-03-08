package br.pucminas.graphtest.controller;


import br.pucminas.graphtest.controller.interfaces.UserController;
import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.model.User;
import br.pucminas.graphtest.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static br.pucminas.graphtest.util.ConstantesRequisicaoUtil.*;
import static br.pucminas.graphtest.util.ConstantesTopicosUtil.USUARIO_CONTROLLER;
import static br.pucminas.graphtest.util.ContrutorRespostaJsonUtil.construirRespostaJSON;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j(topic = USUARIO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_USUARIO)
@AllArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService; //polimorfismo


    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody User usuario) {
        log.info(">>> criar: recebendo requisição para criar usuário");
        User usuarioCriado = userService.criar(usuario);

        return ResponseEntity.created (URI.create("/usuario/" + usuarioCriado.getId())).body (construirRespostaJSON(CHAVES_USUARIO_CONTROLLER, asList(CREATED.value(), MSG_USUARIO_CRIADO, usuarioCriado.getId())));

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
    public ResponseEntity<User> encontrarPorId(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<List<User>> listarTodos() {
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> atualizar(UUID id, User obj) {
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> deletar(UUID id) {
        return null;
    }
}
