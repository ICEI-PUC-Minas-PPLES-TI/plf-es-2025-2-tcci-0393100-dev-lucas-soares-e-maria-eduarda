package br.pucminas.graphtest.adapters.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import java.util.UUID;

@Builder
@JsonPropertyOrder({"id", "perfilUsuario", "nome", "email"})
public record UserDTO(
        UUID id,
        @JsonProperty("perfil_usuario") Integer perfilUsuario,
        String nome,
        String email) {
}
