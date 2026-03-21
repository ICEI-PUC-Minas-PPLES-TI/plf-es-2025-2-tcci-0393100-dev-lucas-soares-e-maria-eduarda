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

/**
 * DTO utilizado para transportar os dados de usuario recebidos e devolvidos pela camada de entrada.
 * <p>
 * Este objeto representa os campos expostos pela API para operacoes de cadastro, atualizacao
 * e consulta de usuarios, alem de concentrar as regras de validacao aplicadas em cada contexto.
 *
 * @param id identificador unico do usuario, usado principalmente em respostas e operacoes que referenciam um registro existente
 * @param profileUser codigo do perfil do usuario no sistema, serializado como {@code perfil_usuario} no JSON
 * @param name nome do usuario utilizado para identificacao e exibicao na aplicacao
 * @param email endereco de email do usuario, usado como dado de contato e identificacao logica
 * @param password senha informada nas requisicoes de criacao, recebida apenas na entrada e nunca retornada na resposta
 */
@Builder
@JsonPropertyOrder({"id", "profileUser", "name", "email", "password"})
public record UserDTO(
        /**
         * Identificador unico do usuario.
         */
        UUID id,

        /**
         * Perfil associado ao usuario, utilizado para definir o nivel de permissao.
         */
        @JsonProperty("perfil_usuario") Integer profileUser,

        /**
         * Nome do usuario validado nas operacoes de criacao e atualizacao.
         */
        @NotBlank(groups = {Create.class, Update.class}, message = "O nome e obrigatorio") String name,

        /**
         * Email do usuario validado nas operacoes de criacao e atualizacao.
         */
        @NotBlank(groups = {Create.class, Update.class}, message = "O email e obrigatorio")
        @Pattern(
                groups = {Create.class, Update.class},
                regexp = "^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.?([a-z]+)?$",
                message = MSG_ERRO_EMAIL
        )
        String email,

        /**
         * Senha usada apenas na criacao do usuario e aceita somente na desserializacao do JSON.
         */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(groups = Create.class, message = "A senha e obrigatoria")
        @Size(groups = Create.class, min = 8, max = 100, message = MSG_ERRO_SENHA)
        String password

) {

    /**
     * Grupo de validacao aplicado quando o DTO e usado na criacao de um novo usuario.
     */
    public interface Create {
    }

    /**
     * Grupo de validacao aplicado quando o DTO e usado na atualizacao de um usuario existente.
     */
    public interface Update {
    }
}
