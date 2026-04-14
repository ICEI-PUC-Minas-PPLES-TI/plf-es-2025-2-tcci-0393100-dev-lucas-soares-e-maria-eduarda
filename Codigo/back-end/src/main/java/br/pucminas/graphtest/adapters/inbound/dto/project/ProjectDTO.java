package br.pucminas.graphtest.adapters.inbound.dto.project;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.pucminas.graphtest.adapters.inbound.util.ValidatorErrorConstantsUtil.MSG_ERRO_CAMPO_EM_BRANCO;

@Builder
@JsonPropertyOrder({"id", "name", "description", "createdAt", "updatedAt"})
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
        String description,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {

    public interface Create {
    }

    public interface Update {
    }
}
