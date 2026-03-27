package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.TokenValidationDTO;
import br.pucminas.graphtest.adapters.inbound.dto.UserDTO;
import jakarta.validation.Valid;
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

import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.ID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO_SENHA;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO_VERIFICAR_TOKEN;

public interface UserController extends BaseCRUDController<UserDTO> {

    @PostMapping
    ResponseEntity<Map<String, Object>> create(
            @Validated(UserDTO.Create.class) @RequestBody UserDTO obj
    );

    @PutMapping(ID)
    ResponseEntity<Map<String, Object>> update(
            @PathVariable UUID id,
            @Validated(UserDTO.Update.class) @RequestBody @NotNull UserDTO obj
    );

    @PatchMapping(USUARIO_SENHA)
    ResponseEntity<Map<String, Object>> updatePassword(@PathVariable UUID id, @Valid @RequestBody PasswordDTO passwordDTO);

    @GetMapping(USUARIO_VERIFICAR_TOKEN)
    ResponseEntity<TokenValidationDTO> verifyToken(@RequestParam("token") String token);
}
