package br.pucminas.graphtest.adapters.inbound.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O email e obrigatorio") String email,

        @NotBlank(message = "A senha e obrigatoria")
        String password
) {
}
