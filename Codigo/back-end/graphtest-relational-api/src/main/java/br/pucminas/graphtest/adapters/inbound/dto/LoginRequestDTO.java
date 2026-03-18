package br.pucminas.graphtest.adapters.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O email e obrigatorio") String email,

        @JsonProperty("senha")
        @NotBlank(message = "A senha e obrigatoria")
        String password
) {
}
