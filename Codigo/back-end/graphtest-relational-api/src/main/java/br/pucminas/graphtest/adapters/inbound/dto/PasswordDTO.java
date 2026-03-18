package br.pucminas.graphtest.adapters.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_SENHA;

@Builder
public record PasswordDTO(
        @JsonProperty("senha_original")
        @NotBlank(message = "A senha original e obrigatoria")
        @Size(min = 8, max = 100, message = MSG_ERRO_SENHA)
        String senhaOriginal,

        @JsonProperty("senha_atualizada")
        @NotBlank(message = "A senha atualizada e obrigatoria")
        @Size(min = 8, max = 100, message = MSG_ERRO_SENHA)
        String senhaAtualizada) {
}
