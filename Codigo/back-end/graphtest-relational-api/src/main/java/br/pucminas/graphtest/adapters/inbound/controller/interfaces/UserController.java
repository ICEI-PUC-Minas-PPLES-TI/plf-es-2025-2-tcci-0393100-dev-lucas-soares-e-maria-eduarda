package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.TokenValidationDTO;
import br.pucminas.graphtest.adapters.inbound.dto.UserDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;
import java.util.UUID;

public interface UserController extends BaseCRUDController<UserDTO> {

    @PostMapping
    ResponseEntity<Map<String, Object>> criar(
            @Validated(UserDTO.Create.class) @RequestBody UserDTO obj
    );

    @PutMapping("/{id}")
    ResponseEntity<Map<String, Object>> atualizar(
            @PathVariable UUID id,
            @Validated(UserDTO.Update.class) @RequestBody @NotNull UserDTO obj
    );

    @PatchMapping("/{id}/senha")
    ResponseEntity<Map<String, Object>> atualizarSenha(@PathVariable UUID id, @RequestBody PasswordDTO passwordDTO);

    @GetMapping("/verificar-token")
    ResponseEntity<TokenValidationDTO> verificarToken(@RequestParam("token") String token);
}
