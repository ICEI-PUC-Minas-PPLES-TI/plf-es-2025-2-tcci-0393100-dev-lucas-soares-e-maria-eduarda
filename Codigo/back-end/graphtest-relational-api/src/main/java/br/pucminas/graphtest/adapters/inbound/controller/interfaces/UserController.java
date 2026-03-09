package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.dto.PasswordDTO;
import br.pucminas.graphtest.dto.UserDTO;
import br.pucminas.graphtest.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

public interface UserController extends BaseCRUDController<UserDTO> {

    @PostMapping
    ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody User obj);

    @PutMapping("/{id}")
    ResponseEntity<Map<String, Object>> atualizar(@PathVariable UUID id, @Valid @RequestBody @NotNull User obj);


    @PutMapping("/atualizar-senha/{id}")
    ResponseEntity<Map<String, Object>> atualizarSenha(@PathVariable UUID id, @RequestBody PasswordDTO passwordDTO);

    @GetMapping("/verificar-token")
    ResponseEntity<?> verificarToken(@RequestParam("token") String token);
}
