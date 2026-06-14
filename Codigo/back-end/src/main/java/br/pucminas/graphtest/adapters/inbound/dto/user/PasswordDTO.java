package br.pucminas.graphtest.adapters.inbound.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_SENHA;

/**
 * DTO utilizado para transportar os dados envolvidos na alteracao de senha de um usuario.
 *
 * @param senhaOriginal senha atual informada para confirmar a identidade do usuario antes da troca
 * @param senhaAtualizada nova senha que sera persistida apos a validacao da solicitacao
 */
@Builder
public record PasswordDTO(
        /**
         * Senha atualmente cadastrada, usada para validar a alteracao solicitada.
         */
        @JsonProperty("senha_original")
        @NotBlank(message = "A senha original e obrigatoria")
        @Size(min = 8, max = 100, message = MSG_ERRO_SENHA)
        String senhaOriginal,

        /**
         * Nova senha que substituira a senha atual apos a validacao.
         */
        @JsonProperty("senha_atualizada")
        @NotBlank(message = "A senha atualizada e obrigatoria")
        @Size(min = 8, max = 100, message = MSG_ERRO_SENHA)
        String senhaAtualizada) {
}
