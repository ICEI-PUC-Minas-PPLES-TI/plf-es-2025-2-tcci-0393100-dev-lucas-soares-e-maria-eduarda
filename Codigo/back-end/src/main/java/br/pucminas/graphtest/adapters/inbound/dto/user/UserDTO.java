package br.pucminas.graphtest.adapters.inbound.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_EMAIL;
import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_SENHA;

@Builder
@JsonPropertyOrder({"id", "perfil_usuario", "name", "email", "createdAt", "updatedAt", "password"})
public record UserDTO(

        UUID id,
        @JsonProperty("perfil_usuario") Integer profileUser,

        @NotBlank(groups = {Create.class, Update.class}, message = "O nome e obrigatorio")
        @Size(min = 10, max = 100)
        String name,

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
        String password,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {

    public interface Create {
    }

    public interface Update {
    }
}
