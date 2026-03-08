package br.pucminas.graphtest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import java.util.UUID;

@Builder
@JsonPropertyOrder({"id", "nome", "email"})
public record UserDTO(
        UUID id,
        String nome,
        String email) {
}
