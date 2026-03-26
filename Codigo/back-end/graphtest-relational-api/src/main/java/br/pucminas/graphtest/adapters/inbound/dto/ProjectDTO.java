package br.pucminas.graphtest.adapters.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_CAMPO_EM_BRANCO;

/**
 * DTO utilizado para transportar os dados de projeto recebidos e devolvidos
 * pela camada de entrada.
 *
 * @param id identificador unico do projeto
 * @param name nome do projeto
 * @param description descricao do projeto
 */
@Builder
@JsonPropertyOrder({"id", "name", "description"})
public record ProjectDTO(

        UUID id,

        @Pattern(
                groups = {Create.class, Update.class},
                regexp = "^(?=.*\\S).*$",
                message = MSG_ERRO_CAMPO_EM_BRANCO
        )
        @Size(groups = {Create.class, Update.class}, min = 3, max = 50)
        String name,

        @Pattern(
                groups = {Create.class, Update.class},
                regexp = "^(?=.*\\S).*$",
                message = MSG_ERRO_CAMPO_EM_BRANCO
        )
        @Size(groups = {Create.class, Update.class}, max = 200)
        String description

) {

    /**
     * Grupo de validacao aplicado quando o DTO e usado na criacao de um novo
     * projeto.
     */
    public interface Create {
    }

    /**
     * Grupo de validacao aplicado quando o DTO e usado na atualizacao de um
     * projeto existente.
     */
    public interface Update {
    }
}
