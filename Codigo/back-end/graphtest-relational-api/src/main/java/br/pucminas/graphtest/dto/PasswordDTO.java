package br.pucminas.graphtest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PasswordDTO(
        @JsonProperty("senha_original") String senhaOriginal,
        @JsonProperty("senha_atualizada") String senhaAtualizada) {
}
