package br.pucminas.graphtest.adapters.inbound.controller.interfaces;

import br.pucminas.graphtest.adapters.inbound.dto.user.PasswordDTO;
import br.pucminas.graphtest.adapters.inbound.dto.security.TokenValidationDTO;
import br.pucminas.graphtest.adapters.inbound.dto.user.UserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO_SENHA;
import static br.pucminas.graphtest.infrastructure.paths.ApiRequestPaths.USUARIO_VERIFICAR_TOKEN;

public interface UserController extends BaseCRUDController<UserDTO, UserDTO, java.util.Map<String, Object>> {

    @PatchMapping(USUARIO_SENHA)
    ResponseEntity<java.util.Map<String, Object>> updatePassword(@PathVariable UUID id, @Valid @RequestBody PasswordDTO passwordDTO);

    @GetMapping(USUARIO_VERIFICAR_TOKEN)
    ResponseEntity<TokenValidationDTO> verifyToken(@RequestParam("token") String token);
}
