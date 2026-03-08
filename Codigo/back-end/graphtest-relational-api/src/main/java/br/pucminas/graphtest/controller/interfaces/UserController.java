package br.pucminas.graphtest.controller.interfaces;

import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

public interface UserController extends BaseCRUDController<User> {

    @PutMapping("/atualizar-senha/{id}")
    ResponseEntity<Map<String, Object>> atualizarSenha(@PathVariable UUID id, @RequestBody PasswordDTO passwordDTO);

    @GetMapping("/verificar-token")
    ResponseEntity<?> verificarToken(@RequestParam("token") String token);
}
