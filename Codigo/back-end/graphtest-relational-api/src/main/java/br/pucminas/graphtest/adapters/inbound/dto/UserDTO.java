package br.pucminas.graphtest.adapters.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_EMAIL;
import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_SENHA;

@Builder
@JsonPropertyOrder({"id", "perfilUsuario", "nome", "email"})
public record UserDTO(
        UUID id,

        @JsonProperty("perfil_usuario") Integer perfilUsuario,

        @NotBlank(groups = {Create.class, Update.class}, message = "O nome e obrigatorio") String nome,

        @NotBlank(groups = {Create.class, Update.class}, message = "O email e obrigatorio")
        @Pattern(
                groups = {Create.class, Update.class},
                regexp = "^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.?([a-z]+)?$",
                message = MSG_ERRO_EMAIL
        )
        String email,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(groups = Create.class, message = "A senha e obrigatoria")
        @Size(groups = Create.class, min = 8, max = 100, message = MSG_ERRO_SENHA)
        String senha) {

    public interface Create {
    }

    public interface Update {
    }
}
